package io.github.sangcomz.diffhawk

import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vcs.ProjectLevelVcsManager
import com.intellij.openapi.vcs.VcsListener
import com.intellij.openapi.wm.CustomStatusBarWidget
import com.intellij.openapi.wm.StatusBar
import com.intellij.ui.ClickListener
import com.intellij.util.messages.MessageBusConnection
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.intellij.util.ui.UIUtil.getRegularPanelInsets
import io.github.sangcomz.diffhawk.renderer.DiffData
import io.github.sangcomz.diffhawk.renderer.RendererFactory
import io.github.sangcomz.diffhawk.renderer.RendererType
import io.github.sangcomz.diffhawk.renderer.WidgetRenderer
import java.awt.FlowLayout
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class DiffHawkWidget(private val project: Project) : CustomStatusBarWidget {
    private var sourceBranch: String = "main"
    private var messageBusConnection: MessageBusConnection? = null
    private var currentRenderer: WidgetRenderer? = null

    private val panel: JPanel = JPanel(FlowLayout(FlowLayout.CENTER, 0, 0)).apply {
        isOpaque = false
        border = JBUI.Borders.empty(getRegularPanelInsets().top, 0, getRegularPanelInsets().bottom, 0)
    }

    init {
        setupRenderer()
        
        val refreshIcon = JLabel(AllIcons.Actions.Refresh)
        refreshIcon.border = JBUI.Borders.emptyLeft(4)
        refreshIcon.toolTipText = "Refresh Diff"
        object : ClickListener() {
            override fun onClick(event: MouseEvent, clickCount: Int): Boolean {
                updateWidget()
                return true
            }
        }.installOn(refreshIcon)

        panel.add(refreshIcon)
    }

    private fun setupRenderer() {
        currentRenderer?.dispose()
        panel.removeAll()
        
        val rendererTypeName = PluginSettingsService.instance.state.rendererType
        val rendererType = RendererType.fromDisplayName(rendererTypeName)
        
        currentRenderer = RendererFactory.createRenderer(rendererType)
        
        panel.add(currentRenderer!!.getComponent(), 0)
        
        currentRenderer!!.setOnClickListener {
            showBranchSelectionPopup()
        }
        
        panel.revalidate()
        panel.repaint()
    }

    override fun getComponent(): JComponent = panel
    override fun ID(): String = "DiffHawkWidget"

    override fun install(statusBar: StatusBar) {
        messageBusConnection = project.messageBus.connect(this)
        messageBusConnection?.subscribe(ProjectLevelVcsManager.VCS_CONFIGURATION_CHANGED, VcsListener {
            updateWidget()
        })

        updateWidget()
    }

    private fun updateWidget() {
        val currentRendererTypeName = PluginSettingsService.instance.state.rendererType
        val currentRendererType = RendererType.fromDisplayName(currentRendererTypeName)
        
        if (currentRenderer == null || currentRenderer!!.getDisplayName() != currentRendererType.displayName) {
            setupRenderer()
        }
        
        val loadingData = DiffData(
            branch = sourceBranch,
            filesChanged = 0,
            insertions = 0,
            deletions = 0,
            total = 0,
            isLoading = true
        )
        currentRenderer?.updateData(loadingData)
        currentRenderer?.setToolTipText("")
        
        ApplicationManager.getApplication().executeOnPooledThread {
            val result = GitDiffCalculator.getDiffStats(project, sourceBranch)
            UIUtil.invokeLaterIfNeeded {
                when (result) {
                    is GitDiffCalculator.DiffResult.Success -> {
                        val stats = result.stats
                        val total = stats.insertions + stats.deletions
                        val limit = PluginSettingsService.instance.state.lineCountLimit
                        
                        val diffData = DiffData(
                            branch = sourceBranch,
                            filesChanged = stats.filesChanged,
                            insertions = stats.insertions,
                            deletions = stats.deletions,
                            total = total,
                            limitCount = limit
                        )
                        
                        currentRenderer?.updateData(diffData)
                        currentRenderer?.setToolTipText("Changes from '$sourceBranch'. Click text to select another branch.")

                        val showAlert = PluginSettingsService.instance.state.showLineCountAlert
                        
                        if (limit > 0 && total > limit && showAlert) {
                            val dialog = LineCountLimitDialog(project, total, limit)
                            if (dialog.showAndGet()) {
                                if (dialog.isDontShowAgainSelected()) {
                                    PluginSettingsService.instance.state.showLineCountAlert = false
                                }
                            }
                        }
                    }
                    is GitDiffCalculator.DiffResult.NotGitRepository -> {
                        val errorData = DiffData(
                            branch = sourceBranch,
                            filesChanged = 0,
                            insertions = 0,
                            deletions = 0,
                            total = 0,
                            errorMessage = "Not a Git project"
                        )
                        currentRenderer?.updateData(errorData)
                        currentRenderer?.setToolTipText("This project does not seem to be a Git repository.")
                    }
                    is GitDiffCalculator.DiffResult.CommandError -> {
                        val errorData = DiffData(
                            branch = sourceBranch,
                            filesChanged = 0,
                            insertions = 0,
                            deletions = 0,
                            total = 0,
                            errorMessage = "Command Error"
                        )
                        currentRenderer?.updateData(errorData)
                        currentRenderer?.setToolTipText(result.errorMessage.ifEmpty { "Failed to run 'git diff'. Check branch name." })
                    }
                }
            }
        }
    }

    private fun showBranchSelectionPopup() {
        val branches = GitBranchUtil.getAllBranches(project)
        if (branches.isEmpty()) return

        JBPopupFactory.getInstance()
            .createPopupChooserBuilder(branches)
            .setTitle("Select Source Branch")
            .setMovable(true)
            .setNamerForFiltering { it }
            .setAdText("Search for branches")
            .setItemChosenCallback { selectedValue ->
                this.sourceBranch = selectedValue
                updateWidget()
            }
            .createPopup()
            .showInCenterOf(panel)
    }

    override fun dispose() {
        currentRenderer?.dispose()
        currentRenderer = null
        messageBusConnection?.disconnect()
        messageBusConnection = null
    }
}
