package io.github.sangcomz.diffhawk.renderer

import com.intellij.ui.ClickListener
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil.getRegularPanelInsets
import io.github.sangcomz.diffhawk.DisplayFormatter
import io.github.sangcomz.diffhawk.PluginSettingsService
import java.awt.FlowLayout
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class TextRenderer : WidgetRenderer {
    private val textLabel = JLabel()
    private val panel: JPanel = JPanel(FlowLayout(FlowLayout.CENTER, 0, 0)).apply {
        isOpaque = false
        border = JBUI.Borders.empty(getRegularPanelInsets().top, 0, getRegularPanelInsets().bottom, 0)
        add(textLabel)
    }
    
    private var clickListener: (() -> Unit)? = null
    
    init {
        object : ClickListener() {
            override fun onClick(event: MouseEvent, clickCount: Int): Boolean {
                clickListener?.invoke()
                return true
            }
        }.installOn(textLabel)
    }
    
    override fun getComponent(): JComponent = panel
    
    override fun updateData(data: DiffData) {
        when {
            data.isLoading -> {
                textLabel.text = " (loading...)"
            }
            data.errorMessage != null -> {
                if (data.errorMessage == "Not a Git project") {
                    textLabel.text = "Not a Git project"
                } else {
                    textLabel.text = "Diff Error"
                }
            }
            else -> {
                val displayFormat = PluginSettingsService.instance.state.displayFormat
                textLabel.text = DisplayFormatter.formatDisplay(
                    template = displayFormat,
                    branch = data.branch,
                    filesChanged = data.filesChanged,
                    insertions = data.insertions,
                    deletions = data.deletions,
                    total = data.total,
                    limitCount = data.limitCount
                )
            }
        }
    }
    
    override fun setToolTipText(text: String?) {
        textLabel.toolTipText = text
    }
    
    override fun setOnClickListener(listener: () -> Unit) {
        this.clickListener = listener
    }
    
    override fun getDisplayName(): String = "Text Format"
}
