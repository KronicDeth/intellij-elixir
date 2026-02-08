package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.espec.Modules

object ESpec {
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
            Modules.erlParametersList() + erlArgumentList,
            Modules.elixirParametersList() + elixirArgumentList
        )
        commandLine.addParameters(mixArgumentList)
        addESpec(commandLine)

        return commandLine
    }

    private fun addESpec(commandLine: GeneralCommandLine) {
        commandLine.addParameter("espec")
    }
}
