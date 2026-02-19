package org.elixir_lang.iex

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.cli.getExecutableFilepathWslSafe
import org.elixir_lang.jps.shared.cli.CliTool
import org.elixir_lang.jps.shared.sdk.SdkPaths

object Mix {
    fun commandLine(
            environment: Map<String, String>,
            workingDirectory: String?,
            elixirSdk: Sdk,
            erlArgumentList: List<String>,
            iexArgumentList: List<String>
    ): GeneralCommandLine {
        val updatedEnvironment = environment.toMutableMap()
        SdkPaths.maybeUpdateMixHome(updatedEnvironment, elixirSdk.homePath)

        val commandLine = org.elixir_lang.IEx.commandLine(updatedEnvironment, workingDirectory, elixirSdk, erlArgumentList)
        commandLine.addParameters(iexArgumentList)
        addMix(commandLine, elixirSdk)
        return commandLine
    }

    private fun addMix(commandLine: GeneralCommandLine, sdk: Sdk) {
        val sdkHomePath = sdk.homePath ?: throw RuntimeException("Missing homePath in SDK: $sdk")
        val mixPath = CliTool.MIX.getExecutableFilepathWslSafe(sdkHomePath)
        commandLine.addParameters("-S", mixPath)
    }
}
