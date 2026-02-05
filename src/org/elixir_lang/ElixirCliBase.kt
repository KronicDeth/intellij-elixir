package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.util.Version

internal object ElixirCliBase {
    /**
     * Keep in-sync with [org.elixir_lang.jps.Builder.addElixir]
     */
    fun addElixirBaseArguments(
        commandLine: GeneralCommandLine,
        elixirSdk: Sdk,
        erlangSdk: Sdk,
        erlArgumentList: kotlin.collections.List<String>,
    ) {
        // MUST be before `-extra` because it turns off `erl` option parsing.
        commandLine.addParameters(erlArgumentList)
        Elixir.prependNewCodePaths(commandLine, elixirSdk, erlangSdk)
        commandLine.addParameters("-noshell", "-s", "elixir", "start_cli")
        commandLine.addParameters("-elixir", "ansi_enabled", "true")
        commandLine.addParameter("-extra")
    }

    fun addIExBaseArguments(
        commandLine: GeneralCommandLine,
        elixirSdk: Sdk,
        erlangSdk: Sdk,
        erlArgumentList: kotlin.collections.List<String>,
    ) {
        Elixir.prependNewCodePaths(commandLine, elixirSdk, erlangSdk)
        commandLine.addParameters("-elixir", "ansi_enabled", "true")
        commandLine.addParameters(erlArgumentList)
        addIExStartArguments(commandLine, elixirSdk)
        commandLine.addParameter("--no-halt")
    }

    private fun addIExStartArguments(commandLine: GeneralCommandLine, elixirSdk: Sdk) {
        commandLine.addParameter("-noshell")

        val elixirVersion = elixirSdk.versionString?.let { Version.parseVersion(it) }
        if (elixirVersion?.lessThan(1, 15, 0) == true) {
            /* Pre Elixir 1.15.0, IEx entrypoint was as Elixir.IEx.CLI start()
             * Pattern: erl -pa <multiple-paths> -noshell -user Elixir.IEx.CLI -extra --no-halt +iex */
            commandLine.addParameters("-user", "Elixir.IEx.CLI")
            commandLine.addParameter("-extra")
        } else if (elixirVersion?.`is`(1, 15, 0) == true) {
            /* Weird case for 1.15.0-rc1 to 1.15.0
             * It had a bugged start_iex that needs to be specified differently.
             * Pattern: erl -pa <multiple-paths> -noshell -user Elixir.IEx.CLI -extra --no-halt +iex
             */
            commandLine.addParameters("-s", "elixir", "start_cli")
            commandLine.addParameters("-user", "elixir")
            commandLine.addParameter("-extra")
            commandLine.addParameters("-e", ":elixir.start_iex()")
        } else if (elixirVersion?.lessThan(1, 17, 0) == true) {
            /* Elixir 1.15.1 - 1.16.x series (e.g., 1.15.1, 1.16.3)
             * Pattern: erl -noshell -elixir_root elixir/lib -pa elixir/lib/elixir/ebin -s elixir start_iex -user elixir -extra --no-halt +iex
             */
            commandLine.addParameters("-s", "elixir", "start_iex")
            commandLine.addParameters("-user", "elixir")
            commandLine.addParameter("-extra")
        } else { // elixirVersion.isAtLeast(1, 17, 0)
            /* Elixir 1.17.x - 1.19.x and potentially future versions if the pattern continues
             * Pattern: erl -noshell -elixir_root elixir/lib -pa elixir/lib/elixir/ebin -user elixir -extra --no-halt +iex
             */
            commandLine.addParameters("-user", "elixir")
            commandLine.addParameter("-extra")
        }
        commandLine.addParameter("+iex")
    }
}
