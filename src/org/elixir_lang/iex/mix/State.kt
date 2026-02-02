package org.elixir_lang.iex.mix

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionException
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.terminal.TerminalExecutionConsole
import org.elixir_lang.console.ElixirConsoleUtil
import org.elixir_lang.notification.setup_sdk.Notifier
import org.elixir_lang.run.WslSafeCommandLineState

class State(environment: ExecutionEnvironment, configuration: Configuration) :
    WslSafeCommandLineState<Configuration>(environment, configuration) {
    @Throws(ExecutionException::class)
    override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
        val project = configuration.project

        val processHandler = startProcess()
        val console = TerminalExecutionConsole(project, processHandler)
        ElixirConsoleUtil.attachFilters(project, console)
        processHandler.startNotify()

        return DefaultExecutionResult(console, processHandler)
    }

    override fun createProcessHandler(process: Process, commandLine: GeneralCommandLine): org.elixir_lang.iex.ProcessHandler =
        org.elixir_lang.iex.ProcessHandler(process, commandLine.commandLineString, commandLine)

    override fun handleExecutionException(e: ExecutionException) {
        Notifier.mixSettings(configuration.ensureModule(), e)
    }
}
