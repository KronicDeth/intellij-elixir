package org.elixir_lang.cli

import com.intellij.execution.ExecutionException
import com.intellij.execution.wsl.WslPath
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.util.system.OS
import org.elixir_lang.notification.setup_sdk.Notifier
import org.elixir_lang.jps.shared.cli.CliArgs
import org.elixir_lang.jps.shared.cli.CliTool
import org.elixir_lang.sdk.erlang_dependent.getErlangSdk
import org.elixir_lang.sdk.erlang_dependent.Type as ErlangDependentType
import org.elixir_lang.jps.shared.cli.CliArguments as SharedCliArguments

class MissingErlangSdkException(sdkName: String) :
    ExecutionException("Elixir SDK '$sdkName' is missing its Erlang SDK dependency")

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

    fun argsOrThrow(
        elixirSdk: Sdk,
        tool: CliTool,
        extraElixirArguments: List<String> = emptyList(),
        extraErlangArguments: List<String> = emptyList(),
        os: OS = effectiveOS(elixirSdk),
        project: Project? = null,
    ): CliArgs {
        val elixirHomePath =
            elixirSdk.homePath
                ?: throw ExecutionException("Elixir SDK home path is not configured")
        val erlangSdk = elixirSdk.getErlangSdk()
        val erlangHomePath = erlangSdk?.homePath
        if (erlangHomePath.isNullOrBlank()) {
            elixirSdk.putUserData(ErlangDependentType.MISSING_ERLANG_SDK_KEY, true)
            Notifier.elixirSdkMissingErlangDependency(project, elixirSdk.name)
            throw MissingErlangSdkException(elixirSdk.name)
        } else {
            elixirSdk.putUserData(ErlangDependentType.MISSING_ERLANG_SDK_KEY, null)
        }

        return SharedCliArguments.args(
            elixirHomePath,
            elixirSdk.versionString,
            erlangHomePath,
            tool,
            extraElixirArguments,
            extraErlangArguments,
            os
        ) ?: throw ExecutionException("Unable to compute CLI arguments for SDK ${elixirSdk.name}")
    }

    private fun effectiveOS(elixirSdk: Sdk): OS {
        if (OS.CURRENT == OS.Windows) {
            elixirSdk.homePath?.let { if (WslPath.isWslUncPath(it)) return OS.Linux }
        }
        return OS.CURRENT
    }
}
