package org.elixir_lang.espec

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionException
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.process.ColoredProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.testframework.TestConsoleProperties
import com.intellij.execution.testframework.autotest.ToggleAutoTestAction
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import com.intellij.execution.ui.ConsoleView
import org.elixir_lang.console.ElixirConsoleUtil
import org.elixir_lang.mix.State
import java.lang.reflect.InvocationTargetException

private const val TEST_FRAMEWORK_NAME = "ESpec"

class State(environment: ExecutionEnvironment, private val configuration: Configuration) :
        CommandLineState(environment) {
    override fun startProcess(): ProcessHandler {
        val commandLine = configuration.commandLine()
        val process = commandLine.createProcess()

        return ColoredProcessHandler(process, commandLine.commandLineString)
    }

    /**
     * Unifies the interface for `SMTestRunnerConnectionUtil.createAndAttachConsole` between 141 and later releases
     */
    private fun createAndAttachConsole(testFrameworkName: String,
                                       processHandler: ProcessHandler,
                                       consoleProperties: TestConsoleProperties): ConsoleView? {
        val klass = SMTestRunnerConnectionUtil::class.java
        var consoleView: ConsoleView? = null

        try {
            val createAndAttachConsole = klass.getMethod("createAndAttachConsole", String::class.java, ProcessHandler::class.java, TestConsoleProperties::class.java)

            try {
                consoleView = createAndAttachConsole.invoke(null, testFrameworkName, processHandler, consoleProperties) as ConsoleView
            } catch (e: IllegalAccessException) {
                LOGGER.error(e)
            } catch (e: InvocationTargetException) {
                LOGGER.error(e)
            }

        } catch (noSuchCreateAndAttachConsole3Method: NoSuchMethodException) {
            try {
                val createAndAttachConsole = klass.getMethod("createAndAttachConsole", String::class.java, ProcessHandler::class.java, TestConsoleProperties::class.java, ExecutionEnvironment::class.java)

                try {
                    consoleView = createAndAttachConsole.invoke(null, testFrameworkName, processHandler, consoleProperties, environment) as ConsoleView
                } catch (e: IllegalAccessException) {
                    LOGGER.error(e)
                } catch (e: InvocationTargetException) {
                    LOGGER.error(e)
                }

            } catch (noSuchCreateAndAttachConsole4Method: NoSuchMethodException) {
                noSuchCreateAndAttachConsole4Method.printStackTrace()
            }

        }

        return consoleView
    }

    @Throws(ExecutionException::class)
    override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
        val processHandler = startProcess()

        val properties = TestConsoleProperties(configuration, TEST_FRAMEWORK_NAME, executor)
        val console = createAndAttachConsole(TEST_FRAMEWORK_NAME, processHandler, properties)
        ElixirConsoleUtil.attachFilters(configuration.project, console!!)

        val executionResult = DefaultExecutionResult(console, processHandler, *createActions(console, processHandler))
        executionResult.setRestartActions(ToggleAutoTestAction())
        return executionResult
    }

    companion object {
        private val LOGGER = com.intellij.openapi.diagnostic.Logger.getInstance(State::class.java)
    }
}
