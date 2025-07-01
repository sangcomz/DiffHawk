package io.github.sangcomz.diffhawk.renderer

import javax.swing.JComponent

interface WidgetRenderer {
    fun getComponent(): JComponent

    fun updateData(data: DiffData)
    
    fun setToolTipText(text: String?)
    
    fun setOnClickListener(listener: () -> Unit)
    
    fun getDisplayName(): String
    
    fun dispose() {}
}
