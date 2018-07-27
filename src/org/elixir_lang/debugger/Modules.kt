package org.elixir_lang.debugger

import java.io.File
import java.io.IOException

object Modules {
    private const val BASE_PATH = "/debugger"
    private const val INTELLIJ_ELIXIR_DEBUGGER_SERVER = "lib/intellij_elixir/debugger/server.ex"
    private const val INTELLIJ_ELIXIR_DEBUGGED = "priv/debugged.exs"
    private val RELATIVE_SOURCE_PATH_LIST = listOf(
            // debugger before debugged as debugged starts debugger
            INTELLIJ_ELIXIR_DEBUGGER_SERVER,
            /* debugged last because it blocks with a receive waiting for the Java debugger to call the Debugger.Server
               with :attach */
            INTELLIJ_ELIXIR_DEBUGGED
    )

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
