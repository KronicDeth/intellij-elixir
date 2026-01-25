package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.Erl.prependCodePaths
import org.elixir_lang.sdk.erlang_dependent.MissingErlangSdk
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
        val erlangSdk = elixirSdkToEnsuredErlangSdk(elixirSdk)
        val commandLine =
            Erl.commandLine(
                pty = false,
                environment = environment,
                workingDirectory = workingDirectory,
                erlangSdk = erlangSdk,
            )
        // MUST be before `addElixir` because it ends with `-extra` which turns off argument parsing for `erl`
        commandLine.addParameters(erlArgumentList)
        addElixir(commandLine, elixirSdk, erlangSdk)

        return commandLine
    }

    fun elixirSdkToEnsuredErlangSdk(elixirSdk: Sdk): Sdk =
        elixirSdkToErlangSdk(elixirSdk)
            ?: throw MissingErlangSdk(elixirSdk)

    fun elixirSdkHasErlangSdk(elixirSdk: Sdk): Boolean = elixirSdkToErlangSdk(elixirSdk) != null

    fun elixirSdkToErlangSdk(elixirSdk: Sdk): Sdk? = elixirSdk.sdkAdditionalData?.let { it as SdkAdditionalData }?.getErlangSdk()

    /**
     * Adds `-pa ebinDirectory` for those in the `elixirSdk` that aren't in the `erlangSdk`
     */
    fun prependNewCodePaths(
        commandLine: GeneralCommandLine,
        elixirSdk: Sdk,
        erlangSdk: Sdk,
    ) {
        val elixirEbinDirectories = elixirSdk.ebinDirectories()
        val erlangEbinDirectories = erlangSdk.ebinDirectories()
        prependNewCodePaths(commandLine, elixirEbinDirectories, erlangEbinDirectories)
    }

    /**
     * Keep in-suync with [org.elixir_lang.jps.Builder.addElixir]
     */
    private fun addElixir(
        commandLine: GeneralCommandLine,
        elixirSdk: Sdk,
        erlangSdk: Sdk,
    ) {
        prependNewCodePaths(commandLine, elixirSdk, erlangSdk)
        commandLine.addParameters("-noshell", "-s", "elixir", "start_cli")
        commandLine.addParameters("-elixir", "ansi_enabled", "true")
        commandLine.addParameter("-extra")
    }

    private fun prependNewCodePaths(
        commandLine: GeneralCommandLine,
        elixirEbinDirectories: kotlin.collections.List<String>,
        erlangEbinDirectories: kotlin.collections.List<String>,
    ) {
        val newEbinDirectories = elixirEbinDirectories - erlangEbinDirectories
        prependCodePaths(commandLine, newEbinDirectories)
    }
}
