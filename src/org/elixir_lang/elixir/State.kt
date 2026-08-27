package org.elixir_lang.elixir

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import org.elixir_lang.notification.setup_sdk.Notifier
import org.elixir_lang.run.ElixirProcessHandler
import org.elixir_lang.run.WslSafeCommandLineState

class State(environment: ExecutionEnvironment, configuration: Configuration) :
    WslSafeCommandLineState<Configuration>(environment, configuration) {

    override fun createProcessHandler(process: Process, commandLine: GeneralCommandLine): ProcessHandler =
        ElixirProcessHandler(process, commandLine.commandLineString)

    override fun handleExecutionException(e: ExecutionException) {
        Notifier.mixSettings(configuration.ensureModule(), e)
    }
}
