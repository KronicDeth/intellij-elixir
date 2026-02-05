package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.Elixir.elixirSdkToEnsuredErlangSdk

object IEx {
    fun commandLine(
        environment: Map<String, String>,
        workingDirectory: String?,
        elixirSdk: Sdk,
        erlArgumentList: kotlin.collections.List<String>
    ): GeneralCommandLine {
        /* `iex` is an alternative mode of the `elixir` script, which changes the `erl` options, so the state needs to
         * be built on top of `erl`, not `elixir`. */
        val erlangSdk = elixirSdkToEnsuredErlangSdk(elixirSdk)
        val commandLine = Erl.commandLine(true, environment, workingDirectory, erlangSdk)
        ElixirCliBase.addIExBaseArguments(commandLine, elixirSdk, erlangSdk, erlArgumentList)

        return commandLine
    }
}
