package org.elixir_lang.debugger

import com.intellij.execution.ExecutionException
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.filters.TextConsoleBuilderImpl
import com.intellij.execution.process.ColoredProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.ConsoleView
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import org.elixir_lang.console.ElixirConsoleUtil
import org.elixir_lang.utils.SetupElixirSDKNotificationListener

class State(environment: ExecutionEnvironment, private val configuration: Configuration) :
        CommandLineState(environment) {
    @Throws(ExecutionException::class)
    override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
        val consoleBuilder = object : TextConsoleBuilderImpl(configuration.project) {
            override fun getConsole(): ConsoleView {
                val consoleView = super.getConsole()
                ElixirConsoleUtil.attachFilters(configuration.project, consoleView)
                return consoleView
            }
        }
        setConsoleBuilder(consoleBuilder)
        val executionResult = super.execute(executor, runner)
        executionResult.processHandler.startNotify()
        return executionResult
    }

    @Throws(ExecutionException::class)
    override fun startProcess(): ProcessHandler {
        val commandLine = configuration.commandLine()

        try {
            return ColoredProcessHandler(commandLine.createProcess(), commandLine.commandLineString)
        } catch (e: ExecutionException) {
            val message = e.message
            val isEmpty = "Executable is not specified" == message
            val notCorrect = message?.startsWith("Cannot run program") ?: false

            if (isEmpty || notCorrect) {
                Notifications.Bus.notify(
                        Notification(
                                "Mix run configuration",
                                "Mix settings",
                                "Mix executable path, elixir executable path, or erl executable path is " +
                                        (if (isEmpty) "empty" else "not specified correctly") +
                                        "<br><a href='configure'>Configure</a></br>",
                                NotificationType.ERROR,
                                SetupElixirSDKNotificationListener(configuration.project, commandLine.workDirectory)
                        )
                )
            }

            throw e
        }
    }
}

