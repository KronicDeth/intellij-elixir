package org.elixir_lang.iex

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.jps.sdk_type.Elixir

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
        addMix(commandLine, elixirSdk)

        return commandLine
    }

    private fun addMix(commandLine: GeneralCommandLine, sdk: Sdk) {
        val mixPath = Elixir.mixPath(sdk.homePath)
        commandLine.addParameters("-S", mixPath)
    }
}
