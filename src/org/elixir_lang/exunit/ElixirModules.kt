package org.elixir_lang.exunit

import java.io.File
import java.io.IOException

object ElixirModules {
    private const val BASE_PATH = "exunit"
    private const val FORMATTER_FILE_NAME = "team_city_ex_unit_formatter.ex"
    private const val FORMATTING_FILE_NAME = "team_city_ex_unit_formatting.ex"

    @Throws(IOException::class)
    private fun copy(): List<File> =
            org.elixir_lang.ElixirModules.copy(BASE_PATH, relativeSourcePathList())

    @Throws(IOException::class)
    fun parametersList(): List<String> = org.elixir_lang.ElixirModules.parametersList(copy())

    private fun relativeSourcePathList(): List<String> = listOf(FORMATTING_FILE_NAME, FORMATTER_FILE_NAME)
}
