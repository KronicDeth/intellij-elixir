package org.elixir_lang.mix

import com.intellij.execution.ExecutionException
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.filters.TextConsoleBuilderImpl
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.module.Module
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.console.ElixirConsoleUtil
import org.elixir_lang.notification.setup_sdk.Notifier
import org.elixir_lang.run.ElixirProcessHandler
import org.elixir_lang.run.WslSafeCommandLineState
import org.elixir_lang.sdk.elixir.Type.Companion.mostSpecificSdk

fun ensureMostSpecificSdk(module: Module): Sdk = mostSpecificSdk(module) ?: throw MissingSdk(module)

/**
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/runner/RebarRunningState.java
 */
class State(environment: ExecutionEnvironment, configuration: Configuration) :
    WslSafeCommandLineState<Configuration>(environment, configuration) {

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

    override fun createProcessHandler(process: Process, commandLine: GeneralCommandLine): ProcessHandler =
        ElixirProcessHandler(process, commandLine.commandLineString)

    override fun handleExecutionException(e: ExecutionException) {
        Notifier.mixSettings(configuration.ensureModule(), e)
    }
}
