package org.elixir_lang.iex

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionException
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.terminal.TerminalExecutionConsole
import org.elixir_lang.console.ElixirConsoleUtil
import org.elixir_lang.notification.setup_sdk.Listener
import org.elixir_lang.notification.setup_sdk.Notifier

class State(environment: ExecutionEnvironment, private val configuration: Configuration) :
        CommandLineState(environment) {
    @Throws(ExecutionException::class)
    override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
        val project = configuration.project

        val processHandler = startProcess()
        val console = TerminalExecutionConsole(project, processHandler)
        ElixirConsoleUtil.attachFilters(project, console)
        processHandler.startNotify()

        return DefaultExecutionResult(console, processHandler)
    }

    @Throws(ExecutionException::class)
    override fun startProcess(): ProcessHandler =
        processHandler().apply {
            /* KillProcessSoftly kills with SIGINT, but SIGINT will just bring up the BREAK VM control menu in iex,
               which we don't want, so kill violently with SIGKILL immediately. */
            setShouldKillProcessSoftly(false)
        }

    @Throws(ExecutionException::class)
    private fun processHandler(): org.elixir_lang.iex.ProcessHandler {
        val commandLine = configuration.commandLine()

        try {
            return ProcessHandler(commandLine)
        } catch (e: ExecutionException) {
            Notifier.mixSettings(configuration.ensureModule(), e)

            throw e
        }
    }
}
