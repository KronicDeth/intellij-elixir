package org.elixir_lang.dialyzer.service

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.module.Module

interface DialyzerService : PersistentStateComponent<State> {
    var mixArguments: String
    var elixirArguments: String
    var erlArguments: String

    fun dialyzerWarnings(module: Module) : List<DialyzerWarn>
}
