package io.github.sangcomz.diffhawk.renderer

import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class ProgressBarRenderer : WidgetRenderer {

    private val progressBar = JProgressBar(0, 100).apply {
        preferredSize = Dimension(150, 20)
        isStringPainted = true
        isBorderPainted = true
        font = font.deriveFont(Font.BOLD, 10f)
    }

    private val panel = JPanel(BorderLayout()).apply {
        add(progressBar, BorderLayout.CENTER)
        isOpaque = false
    }

    private var clickListener: (() -> Unit)? = null

    init {
        setupMouseListener()
    }
    
    override fun getComponent(): JComponent = panel
    
    override fun updateData(data: DiffData) {
        SwingUtilities.invokeLater {
            if (data.isLoading) {
                updateLoadingState()
            } else if (data.errorMessage != null) {
                updateErrorState(data.errorMessage)
            } else {
                updateNormalState(data)
            }
        }
    }
    
    private fun updateLoadingState() {
        progressBar.apply {
            isIndeterminate = true
            string = "Loading..."
        }
    }
    
    private fun updateErrorState(errorMessage: String) {
        progressBar.apply {
            isIndeterminate = false
            value = 0
            string = "Error"
            toolTipText = errorMessage
        }
    }
    
    private fun updateNormalState(data: DiffData) {
        val progressValue = if (data.limitCount > 0) {
            (data.limitProgress * 100).toInt()
        } else {
            val visualProgress = minOf(data.total, 100)
            visualProgress
        }
        
        val displayText = formatDisplayText(data)
        
        progressBar.apply {
            isIndeterminate = false
            value = progressValue
            string = displayText
        }
    }
    
    private fun formatDisplayText(data: DiffData): String {
        return if (data.limitCount > 0) {
            "${data.total}/${data.limitCount} (${String.format("%.0f", data.limitProgress * 100)}%)"
        } else {
            "${data.total} lines"
        }
    }
    
    override fun setToolTipText(text: String?) {
        progressBar.toolTipText = text
        panel.toolTipText = text
    }
    
    override fun setOnClickListener(listener: () -> Unit) {
        this.clickListener = listener
    }
    
    override fun getDisplayName(): String = "Progress Bar"
    
    private fun setupMouseListener() {
        val mouseListener = object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                clickListener?.invoke()
            }
            
            override fun mouseEntered(e: MouseEvent?) {
                progressBar.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            }
            
            override fun mouseExited(e: MouseEvent?) {
                progressBar.cursor = Cursor.getDefaultCursor()
            }
        }
        
        progressBar.addMouseListener(mouseListener)
        panel.addMouseListener(mouseListener)
    }
}
