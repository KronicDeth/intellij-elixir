package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.exunit.ElixirModules

object ExUnit {
    fun commandLine(
        project: Project?,
        environment: Map<String, String>,
        workingDirectory: String?,
        elixirSdk: Sdk,
        erlArgumentList: kotlin.collections.List<String> = emptyList(),
        elixirArgumentList: kotlin.collections.List<String> = emptyList(),
        mixArgumentList: kotlin.collections.List<String> = emptyList()
    ): GeneralCommandLine {
        val commandLine = Mix.commandLine(
            project,
            environment,
            workingDirectory,
            elixirSdk,
            erlArgumentList,
            ElixirModules.parametersList() + elixirArgumentList
        )
        commandLine.addParameters(mixArgumentList)
        addExUnit(commandLine)

        return commandLine
    }

    private fun addExUnit(commandLine: GeneralCommandLine) {
        commandLine.addParameters("test", "--formatter", "TeamCityExUnitFormatter")
    }
}
