package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.util.Version
import org.elixir_lang.jps.shared.ElixirCliArgumentMerger

internal object ElixirCliBase {
    /**
     * Keep in-sync with the JPS builder ElixirCliBase dry-run arguments.
     */
    fun dryRunArguments(
        project: Project?,
        tool: ElixirCliDryRun.Tool,
        environment: Map<String, String>,
        workingDirectory: String?,
        elixirSdk: Sdk,
        erlArgumentList: kotlin.collections.List<String>,
    ): kotlin.collections.List<String>? {
        val baseArguments = if (project != null) {
            ElixirCliDryRunCache.getInstance(project).getOrComputeBaseArguments(
                tool = tool,
                environment = environment,
                workingDirectory = workingDirectory,
                elixirSdk = elixirSdk,
            )
        } else {
            ElixirCliDryRun.baseArguments(tool, environment, workingDirectory, elixirSdk)
        }
        return baseArguments?.let { ElixirCliArgumentMerger.mergeArguments(it, erlArgumentList) }
    }

    /**
     * Keep in-sync with the JPS builder ElixirCliBase fallback arguments.
     */
    fun addFallbackArguments(
        tool: ElixirCliDryRun.Tool,
        commandLine: GeneralCommandLine,
        elixirSdk: Sdk,
        erlangSdk: Sdk,
        erlArgumentList: kotlin.collections.List<String>,
        elixirArgumentList: kotlin.collections.List<String> = emptyList(),
    ) {
        when (tool) {
            ElixirCliDryRun.Tool.ELIXIR -> addElixirFallbackArguments(
                commandLine,
                elixirSdk,
                erlangSdk,
                erlArgumentList
            )
            ElixirCliDryRun.Tool.MIX -> {
                addElixirFallbackArguments(commandLine, elixirSdk, erlangSdk, erlArgumentList)
                addMix(commandLine, elixirSdk, elixirArgumentList)
            }
            ElixirCliDryRun.Tool.IEX -> addIExFallbackArguments(
                commandLine,
                elixirSdk,
                erlangSdk,
                erlArgumentList
            )
        }
    }

    private fun addElixirFallbackArguments(
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

    private fun addMix(
        commandLine: GeneralCommandLine,
        sdk: Sdk,
        elixirArgumentList: kotlin.collections.List<String>,
    ) {
        if (elixirArgumentList.isNotEmpty()) {
            commandLine.addParameters(elixirArgumentList)
        }
        val mixPath = ElixirCliToolPaths.mixPath(sdk.homePath)
        commandLine.addParameter(mixPath)
    }

    private fun addIExFallbackArguments(
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
