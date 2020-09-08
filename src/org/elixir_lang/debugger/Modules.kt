package org.elixir_lang.debugger

import java.io.File
import java.io.IOException

object Modules {
    private const val BASE_PATH = "/debugger"
    private const val INTELLIJ_ELIXIR_DEBUGGER_SERVER = "lib/intellij_elixir/debugger/server.ex"
    private const val INTELLIJ_ELIXIR_DEBUGGED = "lib/intellij_elixir/debugged.ex"
    private const val MIX_TASKS_INTELLIJ_ELIXIR_DEBUG = "lib/mix/tasks/intellij_elixir/debug.ex"
    private val RELATIVE_SOURCE_PATH_LIST = listOf(
            INTELLIJ_ELIXIR_DEBUGGER_SERVER,
            INTELLIJ_ELIXIR_DEBUGGED
    )

    @JvmStatic
    @Throws(IOException::class)
    fun add(parametersList: MutableList<String>): List<String> =
            org.elixir_lang.ElixirModules.add(parametersList, copy())

    private fun copy(mix: Boolean = false): List<File> =
            org.elixir_lang.ElixirModules.copy(BASE_PATH, relativeSourcePathList(mix))

    private fun relativeSourcePathList(mix: Boolean): List<String> =
            RELATIVE_SOURCE_PATH_LIST +
                    if (mix) {
                        listOf(MIX_TASKS_INTELLIJ_ELIXIR_DEBUG)
                    } else {
                        emptyList()
                    }

    fun erlArgumentList(mix: Boolean = false): List<String> =
            listOf("-eval", "'application:ensure_all_started(elixir)'") +
                    copy(mix).flatMap { file ->
                        listOf("-eval", "'Elixir.Code:require_file(\"${file.path}\")'")
                    } + if (mix) {
                emptyList()
            } else  {
                listOf("-eval", "'Elixir.IntelliJElixir.Debugged:start()'")
            }
}
