package org.elixir_lang.iex

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.ElixirCliToolPaths
import org.elixir_lang.sdk.HomePath

object Mix {
    fun commandLine(
            project: Project?,
            environment: Map<String, String>,
            workingDirectory: String?,
            elixirSdk: Sdk,
            erlArgumentList: List<String>,
            iexArgumentList: List<String>
    ): GeneralCommandLine {
        val updatedEnvironment = environment.toMutableMap()
        HomePath.maybeUpdateMixHome(updatedEnvironment, elixirSdk.homePath)

        val commandLine = org.elixir_lang.IEx.commandLine(
            project,
            updatedEnvironment,
            workingDirectory,
            elixirSdk,
            erlArgumentList
        )
        commandLine.addParameters(iexArgumentList)
        addMix(commandLine, elixirSdk)

        return commandLine
    }

    private fun addMix(commandLine: GeneralCommandLine, sdk: Sdk) {
        val mixPath = ElixirCliToolPaths.mixPath(sdk.homePath)
        commandLine.addParameters("-S", mixPath)
    }
}
