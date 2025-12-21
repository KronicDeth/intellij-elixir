package org.elixir_lang.run

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionException
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.testframework.autotest.ToggleAutoTestAction
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import org.elixir_lang.console.ElixirConsoleUtil


abstract class TestRunnerCommandLineState<T>(environment: ExecutionEnvironment, configuration: T) :
    WslSafeCommandLineState<T>(environment, configuration) where T : Configuration, T : HasCommandLine {

    protected abstract val TEST_FRAMEWORK_NAME: String
    protected abstract fun createTestConsoleProperties(executor: Executor): SMTRunnerConsoleProperties

    override val modalProgressMessage: String
        get() = "Starting $TEST_FRAMEWORK_NAME tests"

    override fun createProcessHandler(process: Process, commandLine: GeneralCommandLine): ProcessHandler =
        ElixirProcessHandler(process, commandLine.commandLineString)

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
