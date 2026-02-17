package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.cli.CliArguments
import org.elixir_lang.jps.shared.cli.CliArgs
import org.elixir_lang.jps.shared.cli.CliTool
import org.elixir_lang.jps.shared.sdk.SdkPaths
import org.elixir_lang.run.baseCommandLine

object Mix {
    @JvmStatic
    fun commandLine(
            environment: Map<String, String>,
            workingDirectory: String?,
            elixirSdk: Sdk,
            erlParameters: kotlin.collections.List<String> = emptyList(),
            elixirParameters: kotlin.collections.List<String> = emptyList(),
            pty: Boolean = false,
            project: Project? = null
    ): GeneralCommandLine {
        val updatedEnvironment = environment.toMutableMap()
        SdkPaths.maybeUpdateMixHome(updatedEnvironment, elixirSdk.homePath)

        val args: CliArgs =
            CliArguments.argsOrThrow(
                elixirSdk,
                CliTool.MIX,
                extraElixirArguments = elixirParameters,
                extraErlangArguments = erlParameters,
                project = project,
            )
        val commandLine = baseCommandLine(pty, updatedEnvironment, workingDirectory)
        commandLine.exePath = args.exePath
        commandLine.addParameters(args.arguments)
        return commandLine
    }
}
