package org.elixir_lang.exunit

import org.elixir_lang.Level
import org.elixir_lang.Level.V_1_4
import org.elixir_lang.jps.builder.ParametersList
import java.io.File
import java.io.IOException
import java.util.*

object ElixirModules {
    private const val BASE_PATH = "exunit"
    private const val FORMATTER_FILE_NAME = "team_city_ex_unit_formatter.ex"
    private const val FORMATTING_FILE_NAME = "team_city_ex_unit_formatting.ex"
    private const val MIX_TASK_FILE_NAME = "test_with_formatter.ex"

    private fun addCustomMixTask(relativeSourcePathList: MutableList<String>, level: Level) {
        if (level < V_1_4) {
            relativeSourcePathList.add(MIX_TASK_FILE_NAME)
        }
    }

    private fun addFormatterPath(relativeSourcePathList: MutableList<String>,
                                 level: Level) {
        val versionDirectory : String = when {
            level < V_1_4 -> "1.1.0"
            else -> "1.4.0"
        }

        relativeSourcePathList.add("$versionDirectory/$FORMATTER_FILE_NAME")
    }

    @Throws(IOException::class)
    private fun copy(level: Level): List<File> =
            org.elixir_lang.ElixirModules.copy(BASE_PATH, relativeSourcePathList(level))

    @Throws(IOException::class)
    fun parametersList(level: Level): ParametersList = org.elixir_lang.ElixirModules.parametersList(copy(level))

    private fun relativeSourcePathList(level: Level): List<String> {
        val relativeSourcePathList = ArrayList<String>()
        relativeSourcePathList.add(FORMATTING_FILE_NAME)

        addFormatterPath(relativeSourcePathList, level)
        addCustomMixTask(relativeSourcePathList, level)

        return relativeSourcePathList
    }
}
