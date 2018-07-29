package org.elixir_lang.iex

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.projectRoots.Sdk

object Mix {
    fun commandLine(
            environment: Map<String, String>,
            workingDirectory: String?,
            elixirSdk: Sdk,
            erlArgumentList: List<String>,
            iexArgumentList: List<String>
    ): GeneralCommandLine {
        val commandLine = org.elixir_lang.IEx.commandLine(environment, workingDirectory, elixirSdk, erlArgumentList)
        commandLine.addParameters(iexArgumentList)
        addMix(commandLine)

        return commandLine
    }

    private fun addMix(commandLine: GeneralCommandLine) {
        commandLine.addParameters("-S", "mix")
    }
}
