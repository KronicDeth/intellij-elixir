package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.Elixir.elixirSdkToEnsuredErlangSdk
import org.elixir_lang.Elixir.prependNewCodePaths

object IEx {
    fun commandLine(environment: Map<String, String>, workingDirectory: String?, elixirSdk: Sdk, erlArgumentList: MutableList<String>): GeneralCommandLine {
        /* `iex` is an alternative mode of the `elixir` script, which changes the `erl` options, so the state needs to
           be built on top of `erl`, not `elixir`. */
        val erlangSdk = elixirSdkToEnsuredErlangSdk(elixirSdk)
        val commandLine = org.elixir_lang.Erl.commandLine(environment, workingDirectory, erlangSdk)
        prependNewCodePaths(commandLine, elixirSdk, erlangSdk)
        commandLine.addParameters(erlArgumentList)
        addIEx(commandLine)
        commandLine.addParameter("--no-halt")

        return commandLine
    }

    private fun addIEx(commandLine: GeneralCommandLine) {
        commandLine.addParameter("-noshell")
        commandLine.addParameters("-user", "Elixir.IEx.CLI")
        commandLine.addParameter("-extra")
    }
}
