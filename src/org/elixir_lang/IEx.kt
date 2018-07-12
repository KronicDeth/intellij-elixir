package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.util.SystemInfo
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
        addIEx(commandLine)
        commandLine.addParameter("--no-halt")

        return commandLine
    }

    private fun addIEx(commandLine: GeneralCommandLine) {
        if (!SystemInfo.isWindows) {
            /* `tty_sl -c -e`'s `start_termcap` will fail if `TERM` is not set
               (https://github.com/erlang/otp/blob/360b68d76d8c297d950616f088458b7c239be7ee/erts/emulator/drivers/unix/ttsl_drv.c#L1327-L1332) */
            commandLine.environment.putIfAbsent("TERM", "xterm-256color")
        }

        commandLine.addParameter("-noshell")
        commandLine.addParameters("-user", "Elixir.IEx.CLI")
        commandLine.addParameter("-extra")
    }
}
