package io.github.sangcomz.diffhawk

import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class PluginSettingsConfigurable : Configurable {
    private var settingsPanel: JPanel? = null
    private val exclusionPatternsTextArea = JBTextArea()
    private val settingsService = PluginSettingsService.instance

    private val lineCountLimitField = JBTextField()

    override fun getDisplayName(): String = "Branch Diff Checker"

    override fun createComponent(): JComponent? {
        exclusionPatternsTextArea.text = settingsService.state.exclusionPatterns
        lineCountLimitField.text = settingsService.state.lineCountLimit.toString()

        settingsPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Exclude path patterns (one per line):"), exclusionPatternsTextArea, 1, false)
            .addLabeledComponent(JBLabel("Line count limit (0 to disable):"), lineCountLimitField, 1, false) // 추가
            .addComponentFillVertically(JPanel(), 0)
            .panel
        return settingsPanel
    }

    override fun isModified(): Boolean {
        val limitAsInt = lineCountLimitField.text.toIntOrNull() ?: 0
        return exclusionPatternsTextArea.text != settingsService.state.exclusionPatterns ||
                limitAsInt != settingsService.state.lineCountLimit
    }

    override fun apply() {
        settingsService.state.exclusionPatterns = exclusionPatternsTextArea.text
        settingsService.state.lineCountLimit = lineCountLimitField.text.toIntOrNull() ?: 0
    }

    override fun reset() {
        exclusionPatternsTextArea.text = settingsService.state.exclusionPatterns
        lineCountLimitField.text = settingsService.state.lineCountLimit.toString()
    }
}