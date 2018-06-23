package org.elixir_lang.debugged

import java.io.File
import java.io.IOException

object Modules {
    private const val BASE_PATH = "/debugged"
    private const val INTELLIJ_ELIXIR_DEBUGGED = "lib/intellij_elixir/debugged.ex"
    private val RELATIVE_SOURCE_PATH_LIST = listOf(INTELLIJ_ELIXIR_DEBUGGED)

    @JvmStatic
    @Throws(IOException::class)
    fun add(parametersList: MutableList<String>): List<String> =
            org.elixir_lang.ElixirModules.add(parametersList, copy())

    private fun copy(): List<File> = org.elixir_lang.ElixirModules.copy(BASE_PATH, RELATIVE_SOURCE_PATH_LIST)

    fun erlArgumentList(): List<String> =
            listOf("-eval", "application:ensure_all_started(elixir)") +
                    copy().flatMap { file ->
                        listOf("-eval", "'Elixir.Code':require_file(<<\"${file.path}\">>)")
                    }
}
