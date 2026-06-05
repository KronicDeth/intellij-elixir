package org.elixir_lang.tool_manager

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

/**
 * Project-level settings controlling which tool managers are active.
 *
 * Persisted in `.idea/elixir.xml` under the key `ElixirToolManagerSettings`.
 *
 * **Default behaviour**: all registered tool managers are enabled (opt-out model).
 * An empty [State.disabledManagers] set means "check everything", which preserves
 * the behaviour users had before this setting existed.
 */
@Service(Service.Level.PROJECT)
@State(
    name = "ElixirToolManagerSettings",
    storages = [Storage("elixir.xml")]
)
class ToolManagerSettings : PersistentStateComponent<ToolManagerSettings.State> {

    /** XML-serialisable state.  Fields must be `var` for IntelliJ's reflection-based serialiser. */
    class State {
        var disabledManagers: MutableSet<String> = mutableSetOf()
    }

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    /** Returns `true` when [manager] should be consulted during the notification scan. */
    fun isEnabled(manager: ElixirToolManager): Boolean =
        manager.name !in myState.disabledManagers

    fun setEnabled(manager: ElixirToolManager, enabled: Boolean) {
        if (enabled) myState.disabledManagers.remove(manager.name)
        else myState.disabledManagers.add(manager.name)
    }

    companion object {
        fun getInstance(project: Project): ToolManagerSettings = project.service()
    }
}
