package org.elixir_lang.debugger

import org.elixir_lang.ElixirModules
import java.io.File
import java.io.IOException

object Modules {
    private const val BASE_PATH = "/debugger"
    private const val INTELLIJ_ELIXIR_DEBUGGER_SERVER = "lib/intellij_elixir/debugger/server.ex"
    private const val MIX_TASKS_INTELLIJ_ELIXIR_DEBUG = "lib/mix/tasks/intellij_elixir/debug.ex"
    private val RELATIVE_SOURCE_PATH_LIST = listOf(
            INTELLIJ_ELIXIR_DEBUGGER_SERVER,
            MIX_TASKS_INTELLIJ_ELIXIR_DEBUG
    )

    @JvmStatic
    @Throws(IOException::class)
    fun add(parametersList: MutableList<String>): List<String> =
            org.elixir_lang.ElixirModules.add(parametersList, copy())

    private fun copy(): List<File> = org.elixir_lang.ElixirModules.copy(BASE_PATH, RELATIVE_SOURCE_PATH_LIST)

    fun requiresList(): List<String> = ElixirModules.toRequireList(copy())
}
