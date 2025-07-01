package io.github.sangcomz.diffhawk.renderer

import com.intellij.ui.ClickListener
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil.getRegularPanelInsets
import java.awt.FlowLayout
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class CompactRenderer : WidgetRenderer {
    private val compactLabel = JLabel()
    private val panel: JPanel = JPanel(FlowLayout(FlowLayout.CENTER, 0, 0)).apply {
        isOpaque = false
        border = JBUI.Borders.empty(getRegularPanelInsets().top, 0, getRegularPanelInsets().bottom, 0)
        add(compactLabel)
    }
    
    private var clickListener: (() -> Unit)? = null
    
    init {
        // 클릭 리스너 설정
        object : ClickListener() {
            override fun onClick(event: MouseEvent, clickCount: Int): Boolean {
                clickListener?.invoke()
                return true
            }
        }.installOn(compactLabel)
    }
    
    override fun getComponent(): JComponent = panel
    
    override fun updateData(data: DiffData) {
        when {
            data.isLoading -> {
                compactLabel.text = "⏳"
            }
            data.errorMessage != null -> {
                compactLabel.text = "❌"
            }
            else -> {
                val limitIndicator = if (data.limitCount > 0) {
                    when {
                        data.total > data.limitCount -> "🔴"
                        data.total > data.limitCount * 0.8 -> "🟡"
                        else -> "🟢"
                    }
                } else ""
                
                compactLabel.text = "$limitIndicator${data.branch}[${data.filesChanged}] +${data.insertions}/-${data.deletions}"
            }
        }
    }
    
    override fun setToolTipText(text: String?) {
        compactLabel.toolTipText = text
    }
    
    override fun setOnClickListener(listener: () -> Unit) {
        this.clickListener = listener
    }
    
    override fun getDisplayName(): String = "Compact"
}
