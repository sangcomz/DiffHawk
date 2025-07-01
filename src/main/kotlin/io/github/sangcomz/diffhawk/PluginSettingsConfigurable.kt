package io.github.sangcomz.diffhawk

import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import io.github.sangcomz.diffhawk.renderer.RendererType
import javax.swing.*

class PluginSettingsConfigurable : Configurable {
    private var settingsPanel: JPanel? = null
    private val exclusionPatternsTextArea = JBTextArea()
    private val lineCountLimitField = JBTextField()
    private val showLineCountAlertCheckBox = JBCheckBox("Show line count limit alert")
    private val displayFormatField = JBTextField()
    private val rendererTypeComboBox = JComboBox<String>()
    private val autoRefreshEnabledCheckBox = JBCheckBox("Enable auto refresh")
    private val autoRefreshIntervalField = JBTextField()
    
    private val settingsService = PluginSettingsService.instance

    override fun getDisplayName(): String = "Branch Diff Checker"

    override fun createComponent(): JComponent? {
        exclusionPatternsTextArea.text = settingsService.state.exclusionPatterns
        lineCountLimitField.text = settingsService.state.lineCountLimit.toString()
        showLineCountAlertCheckBox.isSelected = settingsService.state.showLineCountAlert
        displayFormatField.text = settingsService.state.displayFormat
        autoRefreshEnabledCheckBox.isSelected = settingsService.state.autoRefreshEnabled
        autoRefreshIntervalField.text = settingsService.state.autoRefreshIntervalMinutes.toString()

        RendererType.entries.forEach { type ->
            rendererTypeComboBox.addItem(type.displayName)
        }
        rendererTypeComboBox.selectedItem = settingsService.state.rendererType

        val templateHelpLabel = JBLabel("""
            <html>
            <div style='font-size: 11px; color: #666; line-height: 1.3;'>
            <b>Available variables:</b><br>
            • <b>{branch}</b> - Current source branch name (e.g., main)<br>
            • <b>{fileCount}</b> - Number of changed files (e.g., 3)<br>
            • <b>{fileText}</b> - Singular/plural form of 'file' (file or files)<br>
            • <b>{fileChangedText}</b> - Complete file change text (e.g., 3 files changed)<br>
            • <b>{addedLine}</b> - Number of added lines (e.g., 25)<br>
            • <b>{removedLine}</b> - Number of removed lines (e.g., 12)<br>
            • <b>{total}</b> - Total changed lines (added + removed, e.g., 37)<br>
            • <b>{limitCount}</b> - Line count limit setting (e.g., 250 or ∞)<br>
            • <b>{remainingLines}</b> - Remaining lines before limit (e.g., 213 or ∞)<br>
            </div>
            </html>
        """.trimIndent())

        val exampleLabel = JBLabel("""
            <html>
            <div style='font-size: 11px; color: #888; line-height: 1.2;'>
            <b>Example templates:</b><br>
            • <code>{branch} / {fileCount} / {addedLine} / {removedLine} / {total}</code><br>
            • <code>[{branch}] {total}/{limitCount} lines ({remainingLines} left)</code><br>
            • <code>{branch}: +{addedLine} -{removedLine} ({fileCount} {fileText})</code><br>
            </div>
            </html>
        """.trimIndent())

        val autoRefreshPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            add(autoRefreshEnabledCheckBox)
            add(Box.createHorizontalStrut(10))
            add(JBLabel("Interval (minutes):"))
            add(Box.createHorizontalStrut(5))
            add(autoRefreshIntervalField.apply { preferredSize = java.awt.Dimension(60, preferredSize.height) })
        }

        settingsPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Renderer type:"), rendererTypeComboBox, 1, false)
            .addLabeledComponent(JBLabel("Exclude path patterns (one per line):"), exclusionPatternsTextArea, 1, false)
            .addLabeledComponent(JBLabel("Line count limit (0 to disable):"), lineCountLimitField, 1, false)
            .addComponent(showLineCountAlertCheckBox)
            .addComponent(autoRefreshPanel)
            .addLabeledComponent(JBLabel("Display format template:"), displayFormatField, 1, false)
            .addComponent(templateHelpLabel)
            .addComponent(exampleLabel)
            .addComponentFillVertically(JPanel(), 0)
            .panel
        return settingsPanel
    }

    override fun isModified(): Boolean {
        val limitAsInt = lineCountLimitField.text.toIntOrNull() ?: 0
        val intervalAsInt = autoRefreshIntervalField.text.toIntOrNull() ?: 3
        return exclusionPatternsTextArea.text != settingsService.state.exclusionPatterns ||
                limitAsInt != settingsService.state.lineCountLimit ||
                showLineCountAlertCheckBox.isSelected != settingsService.state.showLineCountAlert ||
                displayFormatField.text != settingsService.state.displayFormat ||
                rendererTypeComboBox.selectedItem != settingsService.state.rendererType ||
                autoRefreshEnabledCheckBox.isSelected != settingsService.state.autoRefreshEnabled ||
                intervalAsInt != settingsService.state.autoRefreshIntervalMinutes
    }

    override fun apply() {
        settingsService.state.exclusionPatterns = exclusionPatternsTextArea.text
        settingsService.state.lineCountLimit = lineCountLimitField.text.toIntOrNull() ?: 0
        settingsService.state.showLineCountAlert = showLineCountAlertCheckBox.isSelected
        settingsService.state.displayFormat = displayFormatField.text.ifEmpty { 
            "({branch}) {fileCount} files changed, +{addedLine} / -{removedLine} total:{total}" 
        }
        settingsService.state.rendererType = rendererTypeComboBox.selectedItem as String
        settingsService.state.autoRefreshEnabled = autoRefreshEnabledCheckBox.isSelected
        settingsService.state.autoRefreshIntervalMinutes = autoRefreshIntervalField.text.toIntOrNull()?.coerceAtLeast(1) ?: 3
        
        com.intellij.openapi.application.ApplicationManager.getApplication()
            .messageBus.syncPublisher(SettingsChangeListener.TOPIC).onSettingsChanged()
    }

    override fun reset() {
        exclusionPatternsTextArea.text = settingsService.state.exclusionPatterns
        lineCountLimitField.text = settingsService.state.lineCountLimit.toString()
        showLineCountAlertCheckBox.isSelected = settingsService.state.showLineCountAlert
        displayFormatField.text = settingsService.state.displayFormat
        rendererTypeComboBox.selectedItem = settingsService.state.rendererType
        autoRefreshEnabledCheckBox.isSelected = settingsService.state.autoRefreshEnabled
        autoRefreshIntervalField.text = settingsService.state.autoRefreshIntervalMinutes.toString()
    }
}
