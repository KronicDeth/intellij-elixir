package org.elixir_lang.run

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionException
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.ConsoleView
import com.intellij.platform.ide.progress.runWithModalProgressBlocking
import org.elixir_lang.mix.MixToolingBootstrap
import org.elixir_lang.sdk.elixir.Type.Companion.mostSpecificSdk

abstract class WslSafeCommandLineState<T>(
    environment: ExecutionEnvironment,
    protected val configuration: T
) : CommandLineState(environment) where T : Configuration {

    protected open val modalProgressMessage: String = "Starting ${configuration.name}"

    @Throws(ExecutionException::class)
    override fun startProcess(): ProcessHandler = startProcess(null)

    @Throws(ExecutionException::class)
    protected open fun startProcess(console: ConsoleView?): ProcessHandler {
        try {
            // Build command line and start process in background to avoid EDT violations.
            val (commandLine, process) = runWithModalProgressBlocking(configuration.project, modalProgressMessage) {
                val commandLine = configuration.commandLine()
                val sdk = mostSpecificSdk(configuration.ensureModule())
                MixToolingBootstrap.ensure(commandLine, configuration.project, sdk, console)
                val process = commandLine.createProcess()
                commandLine to process
            }
            return createProcessHandler(process, commandLine)
        } catch (e: ExecutionException) {
            handleExecutionException(e)
            throw e
        }
    }

    @Throws(ExecutionException::class)
    override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
        val console = createConsole(executor)
        val processHandler = startProcess(console)
        console?.attachToProcess(processHandler)
        return DefaultExecutionResult(console, processHandler, *createActions(console, processHandler, executor))
    }

    /**
     * Create the appropriate ProcessHandler for the started process.
     * Subclasses override this to return different ProcessHandler types.
     */
    protected abstract fun createProcessHandler(
        process: Process,
        commandLine: GeneralCommandLine
    ): ProcessHandler

    /**
     * Handle execution exceptions. Default implementation does nothing.
     * Subclasses can override to add custom error notification.
     */
    protected open fun handleExecutionException(e: ExecutionException) {
        // Default: do nothing
    }
}
