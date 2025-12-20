package org.elixir_lang.tests

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
import com.intellij.platform.ide.progress.runWithModalProgressBlocking
import org.elixir_lang.console.ElixirConsoleUtil
import org.elixir_lang.run.Configuration
import org.elixir_lang.run.HasCommandLine
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties


abstract class TestCommandLineState<T>(environment: ExecutionEnvironment, private val configuration: T) :
        CommandLineState(environment) where T : Configuration, T : HasCommandLine {

    protected abstract val TEST_FRAMEWORK_NAME: String
    protected abstract fun createTestConsoleProperties(executor: Executor): SMTRunnerConsoleProperties

    override fun startProcess(): ProcessHandler {
        val commandLine = configuration.commandLine()

        // Create process in background thread to avoid EDT violations with WSL command line patching
        // WSLDistribution.patchCommandLine() requires background thread context
        val process = runWithModalProgressBlocking(configuration.project, "Starting $TEST_FRAMEWORK_NAME tests") {
            commandLine.createProcess()
        }

        return ColoredProcessHandler(process, commandLine.commandLineString)
    }

    @Throws(ExecutionException::class)
    override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
        val processHandler = startProcess()

        val properties = createTestConsoleProperties(executor)
        val console = SMTestRunnerConnectionUtil.createAndAttachConsole(TEST_FRAMEWORK_NAME, processHandler, properties)
        ElixirConsoleUtil.attachFilters(configuration.project, console)

        val executionResult = DefaultExecutionResult(console, processHandler, *createActions(console, processHandler))
        executionResult.setRestartActions(ToggleAutoTestAction())
        return executionResult
    }
}
