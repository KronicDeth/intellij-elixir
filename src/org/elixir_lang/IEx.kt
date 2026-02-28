package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.cli.CliArguments
import org.elixir_lang.jps.shared.cli.CliArgs
import org.elixir_lang.jps.shared.cli.CliTool
import org.elixir_lang.run.baseCommandLine

object IEx {
    fun commandLine(
        environment: Map<String, String>,
        workingDirectory: String?,
        elixirSdk: Sdk,
        erlArgumentList: kotlin.collections.List<String>,
        project: Project? = null,
    ): GeneralCommandLine {
        val args: CliArgs =
            CliArguments.argsOrThrow(
                elixirSdk,
                CliTool.IEX,
                extraErlangArguments = erlArgumentList,
                project = project,
            )
        val commandLine = baseCommandLine(true, environment, workingDirectory)
        commandLine.exePath = args.exePath
        commandLine.addParameters(args.arguments)
        return commandLine
    }
}
