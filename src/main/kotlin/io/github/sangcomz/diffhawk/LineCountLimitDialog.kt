package io.github.sangcomz.diffhawk

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import javax.swing.Action
import javax.swing.JComponent

class LineCountLimitDialog(
    project: Project,
    private val totalLines: Int,
    private val limit: Int
) : DialogWrapper(project, true) {

    private val dontShowAgainCheckBox = JBCheckBox("Don't show this alert again")

    init {
        title = "Line Count Limit Exceeded"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val messageLabel = JBLabel("<html>Total changed lines (<b>$totalLines</b>) exceeded the limit (<b>$limit</b>).</html>")
        
        return FormBuilder.createFormBuilder()
            .addComponent(messageLabel)
            .addVerticalGap(8)
            .addComponent(dontShowAgainCheckBox)
            .panel
    }

    override fun createActions(): Array<Action> {
        return arrayOf(okAction)
    }

    override fun getOKAction(): Action {
        return super.getOKAction().apply {
            putValue(Action.NAME, "OK")
        }
    }

    fun isDontShowAgainSelected(): Boolean {
        return dontShowAgainCheckBox.isSelected
    }
}
