package org.elixir_lang.cli

import com.intellij.execution.wsl.WslPath
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.util.system.OS
import org.elixir_lang.jps.shared.cli.CliArgs
import org.elixir_lang.jps.shared.cli.CliTool
import org.elixir_lang.sdk.erlang_dependent.getErlangSdk
import org.elixir_lang.jps.shared.cli.CliArguments as SharedCliArguments


object CliArguments {
    fun args(
        elixirSdk: Sdk,
        tool: CliTool,
        extraElixirArguments: List<String> = emptyList(),
        extraErlangArguments: List<String> = emptyList(),
        os: OS = effectiveOS(elixirSdk)
    ): CliArgs? {
        return SharedCliArguments.args(
            elixirSdk.homePath,
            elixirSdk.versionString,
            elixirSdk.getErlangSdk()?.homePath,
            tool,
            extraElixirArguments,
            extraErlangArguments,
            os
        )
    }

    private fun effectiveOS(elixirSdk: Sdk): OS {
        if (OS.CURRENT == OS.Windows) {
            elixirSdk.homePath?.let { if (WslPath.isWslUncPath(it)) return OS.Linux }
        }
        return OS.CURRENT
    }
}
