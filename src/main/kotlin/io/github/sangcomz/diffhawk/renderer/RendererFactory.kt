package io.github.sangcomz.diffhawk.renderer

object RendererFactory {
    
    fun createRenderer(type: RendererType): WidgetRenderer {
        return when (type) {
            RendererType.TEXT -> TextRenderer()
            RendererType.COMPACT -> CompactRenderer()
        }
    }
    
    fun getAvailableRenderers(): Array<RendererType> {
        return RendererType.values()
    }
}
