package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
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
        erlArgumentList: kotlin.collections.List<String>
    ): GeneralCommandLine {
        val args: CliArgs = CliArguments.args(elixirSdk, CliTool.IEX, extraErlangArguments = erlArgumentList) ?: throw RuntimeException("Unable to compute CLI arguments for SDK $elixirSdk")
        val commandLine = baseCommandLine(true, environment, workingDirectory)
        commandLine.exePath = args.exePath
        commandLine.addParameters(args.arguments)
        return commandLine
    }
}
