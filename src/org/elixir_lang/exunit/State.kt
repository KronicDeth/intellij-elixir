package org.elixir_lang.exunit

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionException
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.process.ColoredProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.testframework.autotest.ToggleAutoTestAction
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import org.elixir_lang.console.ElixirConsoleUtil
import org.elixir_lang.mix.State

private const val TEST_FRAMEWORK_NAME = "ExUnit"

class State(environment: ExecutionEnvironment, private val configuration: Configuration) :
        CommandLineState(environment) {
    override fun startProcess(): ProcessHandler {
        val commandLine = configuration.commandLine()
        val process = commandLine.createProcess()

        return ColoredProcessHandler(process, commandLine.commandLineString)
    }

    @Throws(ExecutionException::class)
    override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
        val processHandler = startProcess()

        val properties = TestConsoleProperties(configuration, TEST_FRAMEWORK_NAME, executor)
        val console = SMTestRunnerConnectionUtil.createAndAttachConsole(TEST_FRAMEWORK_NAME, processHandler, properties)
        ElixirConsoleUtil.attachFilters(configuration.project, console)

        val executionResult = DefaultExecutionResult(console, processHandler, *createActions(console, processHandler))
        executionResult.setRestartActions(ToggleAutoTestAction())
        return executionResult
    }

    companion object {
        private val LOGGER = com.intellij.openapi.diagnostic.Logger.getInstance(State::class.java)
    }
}
