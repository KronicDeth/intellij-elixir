package org.elixir_lang.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.messages.MessageBus
import com.intellij.util.messages.Topic

interface ElixirExperimentalSettingsListener {
    fun settingsChanged(oldState: ElixirExperimentalSettings.State, newState: ElixirExperimentalSettings.State)
}

@State(
    name = "org.elixir_lang.settings.ElixirExperimentalSettings",
    storages = [Storage("elixir.experimental.xml")]
)
@Service
class ElixirExperimentalSettings : PersistentStateComponent<ElixirExperimentalSettings.State> {
    data class State(
        var enableHtmlInjection: Boolean = false,
        var enableDeleteSdkSmallIDE: Boolean = false,
        var enableStatusBarWidget : Boolean = false,
        var enableLiteralSigilInjection: Boolean = false
    )

    private var elixirSettingsState = State()

    override fun getState(): State = elixirSettingsState

    override fun loadState(state: State) {
        elixirSettingsState = state
    }
    
    fun updateState(newState: State) {
        val oldState = elixirSettingsState.copy()
        elixirSettingsState = newState
        
        // Notify listeners if the settings have actually changed
        if (oldState != newState) {
            val messageBus = com.intellij.openapi.application.ApplicationManager.getApplication().messageBus
            messageBus.syncPublisher(SETTINGS_CHANGED_TOPIC).settingsChanged(oldState, newState)
        }
    }

    companion object {
        @JvmField
        val SETTINGS_CHANGED_TOPIC: Topic<ElixirExperimentalSettingsListener> = Topic.create(
            "ElixirExperimentalSettings.settingsChanged",
            ElixirExperimentalSettingsListener::class.java
        )
        
        val instance: ElixirExperimentalSettings
            get() = com.intellij.openapi.application.ApplicationManager
                .getApplication()
                .getService(ElixirExperimentalSettings::class.java)
    }
}


