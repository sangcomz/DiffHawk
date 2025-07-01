package io.github.sangcomz.diffhawk.renderer

enum class RendererType(val displayName: String) {
    TEXT("Text Format"),
    COMPACT("Compact"),
    PROGRESS_BAR("Progress Bar");
    
    companion object {
        fun fromDisplayName(displayName: String): RendererType {
            return values().find { it.displayName == displayName } ?: TEXT
        }
    }
}
