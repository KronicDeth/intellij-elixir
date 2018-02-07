package org.elixir_lang.utils

import org.elixir_lang.code.Identifier

object ElixirModulesUtil {
    fun elixirModuleNameToErlang(moduleName: String): String =
            when {
                moduleName == "true" || moduleName == "false" || moduleName == "nil" -> moduleName
                moduleName[0] == ':' -> moduleName.substring(1)
                else -> "Elixir." + moduleName
            }

    fun erlangModuleNameToElixir(moduleName: String): String =
            when {
                moduleName == "true" || moduleName == "false" || moduleName == "nil" -> moduleName
                moduleName.startsWith("Elixir.") -> moduleName.removePrefix("Elixir.")
                else -> ":" + Identifier.inspectAsFunction(moduleName)
            }
}
