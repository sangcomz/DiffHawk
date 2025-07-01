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
import com.intellij.ui.awt.RelativePoint
import com.intellij.util.messages.MessageBusConnection
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.intellij.util.ui.UIUtil.getRegularPanelInsets
import java.awt.FlowLayout
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class DiffHawkWidget(private val project: Project) : CustomStatusBarWidget {
    private val textLabel = JLabel()
    private var sourceBranch: String = "main"
    private var messageBusConnection: MessageBusConnection? = null

    private val panel: JPanel = JPanel(FlowLayout(FlowLayout.CENTER, 0, 0)).apply {
        isOpaque = false
        border = JBUI.Borders.empty(getRegularPanelInsets().top, 0, getRegularPanelInsets().bottom, 0)
    }

    init {
        val refreshIcon = JLabel(AllIcons.Actions.Refresh)
        refreshIcon.border = JBUI.Borders.emptyLeft(4)
        refreshIcon.toolTipText = "Refresh Diff"
        object : ClickListener() {
            override fun onClick(event: MouseEvent, clickCount: Int): Boolean {
                updateWidget()
                return true
            }
        }.installOn(refreshIcon)

        panel.add(textLabel)
        panel.add(refreshIcon)
    }

    override fun getComponent(): JComponent = panel
    override fun ID(): String = "DiffHawkWidget"

    override fun install(statusBar: StatusBar) {
        messageBusConnection = project.messageBus.connect(this)
        messageBusConnection?.subscribe(ProjectLevelVcsManager.VCS_CONFIGURATION_CHANGED, VcsListener {
            updateWidget()
        })

        object : ClickListener() {
            override fun onClick(event: MouseEvent, clickCount: Int): Boolean {
                showBranchSelectionPopup(event)
                return true
            }
        }.installOn(textLabel)
        updateWidget()
    }

    private fun updateWidget() {
        textLabel.text = " (loading...)"
        textLabel.toolTipText = ""
        ApplicationManager.getApplication().executeOnPooledThread {
            val result = GitDiffCalculator.getDiffStats(project, sourceBranch)
            UIUtil.invokeLaterIfNeeded {
                when (result) {
                    is GitDiffCalculator.DiffResult.Success -> {
                        val stats = result.stats
                        val total = stats.insertions + stats.deletions
                        val fileText = if (stats.filesChanged == 1) "${stats.filesChanged} file changed" else "${stats.filesChanged} files changed"
                        textLabel.text = "($sourceBranch) $fileText, +${stats.insertions} / -${stats.deletions} total:$total"
                        textLabel.toolTipText = "Changes from '$sourceBranch'. Click text to select another branch."

                        val limit = PluginSettingsService.instance.state.lineCountLimit
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
                        textLabel.text = "Not a Git project"
                        textLabel.toolTipText = "This project does not seem to be a Git repository."
                    }
                    is GitDiffCalculator.DiffResult.CommandError -> {
                        textLabel.text = "Diff Error"
                        textLabel.toolTipText = result.errorMessage.ifEmpty { "Failed to run 'git diff'. Check branch name." }
                    }
                }
            }
        }
    }

    private fun showBranchSelectionPopup(e: MouseEvent) {
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
            .show(RelativePoint(e.component, e.point))
    }

    override fun dispose() {
        messageBusConnection?.disconnect()
        messageBusConnection = null
    }
}
