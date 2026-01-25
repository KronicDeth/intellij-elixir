package org.elixir_lang.distillery

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionException
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.filters.TextConsoleBuilderImpl
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.ConsoleView
import com.intellij.terminal.TerminalExecutionConsole
import org.elixir_lang.console.ElixirConsoleUtil
import org.elixir_lang.notification.setup_sdk.Notifier
import org.elixir_lang.run.ElixirProcessHandler
import org.elixir_lang.run.WslSafeCommandLineState

class State(environment: ExecutionEnvironment, configuration: Configuration) :
    WslSafeCommandLineState<Configuration>(environment, configuration) {

    @Throws(ExecutionException::class)
    override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
        val project = configuration.project

        return if (configuration.pty) {
            val processHandler = startProcess()
            val console = TerminalExecutionConsole(project, processHandler)
            ElixirConsoleUtil.attachFilters(project, console)
            processHandler.startNotify()

            DefaultExecutionResult(console, processHandler)
        } else {
            val consoleBuilder = object : TextConsoleBuilderImpl(configuration.project) {
                override fun getConsole(): ConsoleView {
                    val consoleView = super.getConsole()
                    ElixirConsoleUtil.attachFilters(configuration.project, consoleView)
                    return consoleView
                }
            }
            setConsoleBuilder(consoleBuilder)

            super.execute(executor, runner)
        }
    }

    override fun createProcessHandler(process: Process, commandLine: GeneralCommandLine): ProcessHandler {
        return if (configuration.pty) {
            // PTY mode uses IEX-specific handler with terminal formatting
            org.elixir_lang.iex.ProcessHandler(process, commandLine.commandLineString, commandLine)
        } else {
            // Non-PTY mode uses standard Elixir handler with double-SIGINT logic
            ElixirProcessHandler(process, commandLine.commandLineString)
        }
    }

    override fun handleExecutionException(e: ExecutionException) {
        Notifier.mixSettings(configuration.ensureModule(), e)
    }
}
