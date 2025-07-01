package io.github.sangcomz.diffhawk

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

data class PluginSettingsState(
    var exclusionPatterns: String = """
        **/test/**
        **/*.gradle.kts
        **/*.xml
        **/*.yml
        **/*.sh
        **/*.pro
    """.trimIndent(),

    var lineCountLimit: Int = 250,
    var showLineCountAlert: Boolean = true,
    var displayFormat: String = "({branch}) {fileCount} files changed, +{addedLine} / -{removedLine} total:{total}",
    var rendererType: String = "Text Format"
)

@State(
    name = "io.github.sangcomz.diffhawk.PluginSettingsState",
    storages = [Storage("diffHawkSettings.xml")]
)
class PluginSettingsService : PersistentStateComponent<PluginSettingsState> {

    private var internalState = PluginSettingsState()

    override fun getState(): PluginSettingsState {
        return internalState
    }

    override fun loadState(state: PluginSettingsState) {
        internalState = state
    }

    companion object {
        val instance: PluginSettingsService
            get() = ApplicationManager.getApplication().getService(PluginSettingsService::class.java)
    }
}
