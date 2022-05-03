package org.elixir_lang.credo

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

@com.intellij.openapi.components.State(name = "Credo", storages = [Storage(value = "credo.xml")])
class Service : PersistentStateComponent<State?> {
    private var state = State()
    override fun getState(): State? = state

    override fun loadState(state: State) {
        this.state = state
    }

    fun includeExplanation(): Boolean = state.includeExplanation

    fun includeExplanation(includeExplanation: Boolean) {
        state.includeExplanation = includeExplanation
    }

    companion object {
        @JvmStatic
        fun getInstance(project: Project): Service = project.getService(Service::class.java) ?: Service()
    }
}
