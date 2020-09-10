package org.elixir_lang.dialyzer.service

import com.intellij.openapi.components.PersistentStateComponent

interface DialyzerService : PersistentStateComponent<State> {
    var mixDialyzerCommand: String
    fun getDialyzerWarnings() : List<DialyzerWarn>
}
