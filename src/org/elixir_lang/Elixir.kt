package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.Erl.prependCodePaths
import org.elixir_lang.sdk.erlang_dependent.MissingErlangSdk
import org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData

object Elixir {
    /**
     * Keep in-sync with the JPS builder elixir command line.
     */
    fun commandLine(
        project: Project?,
        environment: Map<String, String>,
        workingDirectory: String?,
        elixirSdk: Sdk,
        erlArgumentList: kotlin.collections.List<String> = emptyList(),
    ): GeneralCommandLine {
        return ElixirCliCommandLine.commandLine(
            project = project,
            tool = ElixirCliDryRun.Tool.ELIXIR,
            pty = false,
            environment = environment,
            workingDirectory = workingDirectory,
            elixirSdk = elixirSdk,
            erlArgumentList = erlArgumentList,
        )
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

    private fun prependNewCodePaths(
        commandLine: GeneralCommandLine,
        elixirEbinDirectories: kotlin.collections.List<String>,
        erlangEbinDirectories: kotlin.collections.List<String>,
    ) {
        val newEbinDirectories = elixirEbinDirectories - erlangEbinDirectories
        prependCodePaths(commandLine, newEbinDirectories)
    }
}
