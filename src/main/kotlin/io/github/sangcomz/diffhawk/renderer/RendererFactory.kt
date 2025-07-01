package io.github.sangcomz.diffhawk.renderer

object RendererFactory {
    
    fun createRenderer(type: RendererType): WidgetRenderer {
        return when (type) {
            RendererType.TEXT -> TextRenderer()
            RendererType.COMPACT -> CompactRenderer()
            RendererType.PROGRESS_BAR -> ProgressBarRenderer()
        }
    }
    
    fun getAvailableRenderers(): Array<RendererType> {
        return RendererType.values()
    }
}
