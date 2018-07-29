package org.elixir_lang.debugger.configuration

data class InheritedModuleFilter(val inherited: Boolean = true, val enabled: Boolean = true, val pattern: String = "")
