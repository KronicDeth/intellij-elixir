package org.elixir_lang.mix

import com.intellij.execution.ExecutionException
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.filters.TextConsoleBuilderImpl
import com.intellij.execution.process.ColoredProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.ConsoleView
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.module.Module
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.console.ElixirConsoleUtil
import org.elixir_lang.sdk.elixir.Type.mostSpecificSdk
import org.elixir_lang.utils.SetupElixirSDKNotificationListener

fun ensureMostSpecificSdk(module: Module): Sdk = mostSpecificSdk(module) ?: throw MissingSdk(module)

/**
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/runner/RebarRunningState.java
 */
class State(environment: ExecutionEnvironment, private val configuration: Configuration) :
        CommandLineState(environment) {
    fun createConsoleView(executor: Executor): ConsoleView =
            TextConsoleBuilderFactory.getInstance().createBuilder(configuration.project).console

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
        return super.execute(executor, runner)
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

