package org.elixir_lang.credo

import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

@com.intellij.openapi.components.State(name = "Credo", storages = [Storage(value = "credo.xml")])
class Service : PersistentStateComponent<State?> {
    private var state = State()
    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    var elixirArguments: String
        get() = state.elixirArguments
        set(value) {
            state.elixirArguments = value
        }

    var erlArguments: String
        get() = state.erlArguments
        set(value) {
            state.erlArguments = value
        }

    var environmentVariableData: EnvironmentVariablesData
        get() = state.environmentVariableData.toConfiguration()
        set(value) {
            state.environmentVariableData =
                org.elixir_lang.credo.state.EnvironmentVariablesData.fromConfiguration(value)
        }

    companion object {
        @JvmStatic
        fun getInstance(project: Project): Service = project.getService(Service::class.java) ?: Service()
    }
}
