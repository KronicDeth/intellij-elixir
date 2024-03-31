package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.util.Version
import org.elixir_lang.Elixir.elixirSdkToEnsuredErlangSdk
import org.elixir_lang.Elixir.prependNewCodePaths

object IEx {
    fun commandLine(
            environment: Map<String, String>,
            workingDirectory: String?,
            elixirSdk: Sdk,
            erlArgumentList: kotlin.collections.List<String>
    ): GeneralCommandLine {
        /* `iex` is an alternative mode of the `elixir` script, which changes the `erl` options, so the state needs to
           be built on top of `erl`, not `elixir`. */
        val erlangSdk = elixirSdkToEnsuredErlangSdk(elixirSdk)
        val commandLine = org.elixir_lang.Erl.commandLine(true, environment, workingDirectory, erlangSdk)
        prependNewCodePaths(commandLine, elixirSdk, erlangSdk)
        commandLine.addParameters("-elixir", "ansi_enabled", "true")
        commandLine.addParameters(erlArgumentList)
        addIEx(commandLine, elixirSdk)
        commandLine.addParameter("--no-halt")

        return commandLine
    }

    private fun addIEx(commandLine: GeneralCommandLine, elixirSdk: Sdk) {
        commandLine.addParameter("-noshell")

        val elixirVersion = elixirSdk.versionString?.let { Version.parseVersion(it) }
        if (elixirVersion?.lessThan(1, 15, 0) == true) {
            /* Pre Elixir 1.15.0, IEx entrypoint was as Elixir.IEx.CLI start() */
            commandLine.addParameters("-user", "Elixir.IEx.CLI")
            commandLine.addParameter("-extra")
        } else if (elixirVersion?.`is`(1, 15, 0) == true) {
            /* Weird case for 1.15.0-rc1 to 1.15.0
             * It had a bugged start_iex that needs to be specified differently. */
            commandLine.addParameters("-s", "elixir", "start_cli")
            commandLine.addParameters("-user", "elixir")
            commandLine.addParameter("-extra")
            commandLine.addParameters("-e", ":elixir.start_iex()")
        } else {
            /* Case for elixir 1.16.0+ (and 1.15.1+ from the 1.15 branch).
             * We can call start_iex directly from elixir entrypoint. */
            commandLine.addParameters("-s", "elixir", "start_iex")
            commandLine.addParameters("-user", "elixir")
            commandLine.addParameter("-extra")
        }
        commandLine.addParameter("+iex")
    }
}
