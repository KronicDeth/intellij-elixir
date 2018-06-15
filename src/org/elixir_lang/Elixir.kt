package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.OrderRootType
import org.elixir_lang.Erl.prependCodePaths
import org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData

object Elixir {
    /**
     * Keep in-sync with [org.elixir_lang.jps.Builder.elixirCommandLine]
     */
    fun commandLine(
            environment: Map<String, String>,
            workingDirectory: String?,
            elixirSdk: Sdk,
            erlParameters: kotlin.collections.List<String> = emptyList()
    ): GeneralCommandLine {
        val erlangSdk = elixirSdkToEnsuredErlangSdk(elixirSdk)
        val commandLine = org.elixir_lang.Erl.commandLine(environment, workingDirectory, erlangSdk)
        addElixir(commandLine, elixirSdk, erlangSdk)
        commandLine.addParameters(erlParameters)

        return commandLine
    }

    fun elixirSdkToEnsuredErlangSdk(elixirSdk: Sdk): Sdk =
            elixirSdk.sdkAdditionalData.let { it as SdkAdditionalData }.ensureErlangSdk()

    /**
     * Adds `-pa ebinDirectory` for those in the `elixirSdk` that aren't in the `erlangSdk`
     */
    fun prependNewCodePaths(commandLine: GeneralCommandLine, elixirSdk: Sdk, erlangSdk: Sdk) {
        val elixirEbinDirectories = elixirSdk.rootProvider.getFiles(OrderRootType.CLASSES).map { it.canonicalPath!! }
        val erlangEbinDirectories = erlangSdk.rootProvider.getFiles(OrderRootType.CLASSES).map { it.canonicalPath!! }
        prependNewCodePaths(commandLine, elixirEbinDirectories, erlangEbinDirectories)
    }

    /**
     * Keep in-suync with [org.elixir_lang.jps.Builder.addElixir]
     */
    private fun addElixir(commandLine: GeneralCommandLine, elixirSdk: Sdk, erlangSdk: Sdk) {
        prependNewCodePaths(commandLine, elixirSdk, erlangSdk)
        commandLine.addParameters("-noshell", "-s", "elixir", "start_cli")
        commandLine.addParameter("-extra")
    }

    private fun prependNewCodePaths(
            commandLine: GeneralCommandLine,
            elixirEbinDirectories: kotlin.collections.List<String>,
            erlangEbinDirectories: kotlin.collections.List<String>
    ) {
        val newEbinDirectories = elixirEbinDirectories - erlangEbinDirectories
        prependCodePaths(commandLine, newEbinDirectories)
    }
}
