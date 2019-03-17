package org.elixir_lang.espec

import java.io.File
import java.io.IOException

object Modules {
    fun erlParametersList(): List<String> = listOf("-eval", "application:set_env(espec, formatters, [{'Elixir.TeamCityESpecFormatter', #{}}])")

    @Throws(IOException::class)
    fun elixirParametersList(): List<String> = org.elixir_lang.ElixirModules.parametersList(copy())

    private const val BASE_PATH = "espec"
    private const val FORMATTER_FILE_NAME = "team_city_espec_formatter.ex"
    private val RELATIVE_SOURCE_PATH_LIST = listOf(FORMATTER_FILE_NAME)

    @Throws(IOException::class)
    private fun copy(): List<File> =
            org.elixir_lang.ElixirModules.copy(BASE_PATH, RELATIVE_SOURCE_PATH_LIST)
}
