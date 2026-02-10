package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk

object IEx {
    fun commandLine(
        project: Project?,
        environment: Map<String, String>,
        workingDirectory: String?,
        elixirSdk: Sdk,
        erlArgumentList: kotlin.collections.List<String>
    ): GeneralCommandLine {
        /* `iex` is an alternative mode of the `elixir` script, which changes the `erl` options, so the state needs to
         * be built on top of `erl`, not `elixir`. */
        return ElixirCliCommandLine.commandLine(
            project = project,
            tool = ElixirCliDryRun.Tool.IEX,
            pty = true,
            environment = environment,
            workingDirectory = workingDirectory,
            elixirSdk = elixirSdk,
            erlArgumentList = erlArgumentList,
        )
    }
}
