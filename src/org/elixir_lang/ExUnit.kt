package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.exunit.ElixirModules
import org.elixir_lang.file.LevelPropertyPusher.level

object ExUnit {
    fun commandLine(environment: Map<String, String>,
                    workingDirectory: String?,
                    elixirSdk: Sdk,
                    erlArgumentList: kotlin.collections.List<String> = emptyList(),
                    elixirArgumentList: kotlin.collections.List<String> = emptyList(),
                    mixArgumentList: kotlin.collections.List<String> = emptyList()
    ): GeneralCommandLine {
        val commandLine = org.elixir_lang.Mix.commandLine(
                environment,
                workingDirectory,
                elixirSdk,
                erlArgumentList,
                ElixirModules.parametersList(level(elixirSdk)) + elixirArgumentList
        )
        commandLine.addParameters(mixArgumentList)
        addExUnit(commandLine, elixirSdk)

        return commandLine
    }

    private fun addExUnit(commandLine: GeneralCommandLine, sdk: Sdk) {
        commandLine.addParameter(task(sdk))
        commandLine.addParameters("--formatter", "TeamCityExUnitFormatter")
    }

    private fun task(sdk: Sdk): String {
        return if (level(sdk) >= Level.V_1_4) {
            "test"
        } else {
            "test_with_formatter"
        }
    }
}
