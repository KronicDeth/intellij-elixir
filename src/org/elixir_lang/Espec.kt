package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.projectRoots.Sdk

object Espec {
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
                elixirArgumentList
        )
        commandLine.addParameters(mixArgumentList)
        addEspec(commandLine)

        return commandLine
    }

    private fun addEspec(commandLine: GeneralCommandLine) {
        commandLine.addParameter("espec")
    }
}
