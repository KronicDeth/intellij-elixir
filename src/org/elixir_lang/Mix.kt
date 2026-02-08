package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.sdk.HomePath

object Mix {
    /**
     * Keep in-sync with the JPS builder mix command line.
     */
    @JvmStatic
    fun commandLine(
            project: Project?,
            environment: Map<String, String>,
            workingDirectory: String?,
            elixirSdk: Sdk,
            erlParameters: kotlin.collections.List<String> = emptyList(),
            elixirParameters: kotlin.collections.List<String> = emptyList()
    ): GeneralCommandLine {
        val updatedEnvironment = environment.toMutableMap()
        HomePath.maybeUpdateMixHome(updatedEnvironment, elixirSdk.homePath)

        val commandLine = ElixirCliCommandLine.commandLine(
            project = project,
            tool = ElixirCliDryRun.Tool.MIX,
            pty = false,
            environment = updatedEnvironment,
            workingDirectory = workingDirectory,
            elixirSdk = elixirSdk,
            erlArgumentList = erlParameters,
            elixirArgumentList = elixirParameters,
        )

        return commandLine
    }
}
