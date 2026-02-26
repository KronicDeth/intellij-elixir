package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.cli.CliArguments
import org.elixir_lang.jps.shared.cli.CliArgs
import org.elixir_lang.jps.shared.cli.CliTool
import org.elixir_lang.run.baseCommandLine
import org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData

object Elixir {
    /**
     * Keep in-sync with [org.elixir_lang.jps.Builder.elixirCommandLine]
     */
    fun commandLine(
        environment: Map<String, String>,
        workingDirectory: String?,
        elixirSdk: Sdk,
        erlArgumentList: kotlin.collections.List<String> = emptyList(),
    ): GeneralCommandLine {
        val args: CliArgs = CliArguments.args(elixirSdk, CliTool.ELIXIR, extraErlangArguments = erlArgumentList) ?: throw RuntimeException("Unable to compute CLI arguments for SDK $elixirSdk")
        val commandLine = baseCommandLine(false, environment, workingDirectory)
        commandLine.exePath = args.exePath
        commandLine.addParameters(args.arguments)
        return commandLine
    }

    fun elixirSdkHasErlangSdk(elixirSdk: Sdk): Boolean = elixirSdkToErlangSdk(elixirSdk) != null
    fun elixirSdkToErlangSdk(elixirSdk: Sdk): Sdk? = elixirSdk.sdkAdditionalData?.let { it as SdkAdditionalData }?.getErlangSdk()
}
