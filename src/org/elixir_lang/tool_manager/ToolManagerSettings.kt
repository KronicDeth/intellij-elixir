package org.elixir_lang.tool_manager

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.util.messages.Topic

/** Listener notified whenever a tool manager is enabled or disabled. */
fun interface ToolManagerSettingsListener {
    fun toolManagerSettingsChanged()
}

/**
 * Project-level settings controlling which tool managers are active.
 *
 * Persisted in `.idea/elixir.xml` under the key `ElixirToolManagerSettings`.
 *
 * Registered only on rich IDEs (via `rich-platform-plugin.xml`) because the SDK
 * configuration performed by tool managers relies on Java-module APIs.  No `@Service`
 * annotation is present so the platform does not auto-register this class for all IDEs.
 *
 * **Default behaviour**: all tool managers are **disabled** (opt-in model).
 * A tool manager must be explicitly enabled by the user before it is consulted
 * during the SDK notification scan.  This ensures that experimental managers
 * (such as mise) do not run without user consent.
 *
 * An empty [State.enabledManagers] set therefore means "no tool managers are active".
 */
@Suppress("LightServiceMigrationCode")
@State(
    name = "ElixirToolManagerSettings",
    storages = [Storage("elixir.xml")]
)
internal class ToolManagerSettings : PersistentStateComponent<ToolManagerSettings.State> {

    /** XML-serialisable state.  Fields must be `var` for IntelliJ's reflection-based serialiser. */
    class State {
        var enabledManagers: MutableSet<String> = mutableSetOf()
    }

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    /** Returns `true` when [manager] has been explicitly enabled by the user. */
    fun isEnabled(manager: ElixirToolManager): Boolean =
        manager.name in myState.enabledManagers

    fun setEnabled(manager: ElixirToolManager, enabled: Boolean) {
        if (enabled) myState.enabledManagers.add(manager.name)
        else myState.enabledManagers.remove(manager.name)
    }

    companion object {
        @JvmField
        val SETTINGS_CHANGED_TOPIC: Topic<ToolManagerSettingsListener> = Topic.create(
            "ToolManagerSettings.changed",
            ToolManagerSettingsListener::class.java
        )

        fun getInstance(project: Project): ToolManagerSettings = project.service()
    }
}
