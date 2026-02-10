package org.elixir_lang.mix

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.*
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.Mix
import org.elixir_lang.jps.shared.mix.MixToolingBootstrapSpec
import org.elixir_lang.settings.ElixirExperimentalSettings
import java.io.File

internal object MixToolingBootstrap {
    private val logger = Logger.getInstance(MixToolingBootstrap::class.java)
    private val MIX_EXECUTABLES = setOf("mix", "mix.bat", "mix.cmd")
    private const val DRY_RUN_ENV = "ELIXIR_CLI_DRY_RUN"

    @Throws(ExecutionException::class)
    fun ensure(
        commandLine: GeneralCommandLine,
        project: Project?,
        sdk: Sdk?,
        console: ConsoleView? = null
    ) {
        if (!isEnabled()) {
            return
        }

        val invocation = parseMixInvocation(commandLine) ?: return
        if (commandLine.environment.containsKey(DRY_RUN_ENV)) {
            return
        }

        val tasks = extractTasks(invocation.mixArguments)
        if (!MixToolingBootstrapSpec.shouldBootstrapForTasks(tasks)) {
            return
        }

        if (sdk == null) {
            logger.warn("Skipping mix tooling bootstrap: missing Elixir SDK")
            return
        }

        for (task in MixToolingBootstrapSpec.TASKS) {
            val mixToolingBootstrapCommandLine = buildMixToolingBootstrapCommandLine(commandLine, project, sdk, task)
            runMixToolingBootstrapTask(mixToolingBootstrapCommandLine, task, project, console)
        }
    }

    private fun parseMixInvocation(commandLine: GeneralCommandLine): MixInvocation? {
        val parameters = commandLine.parametersList.list
        if (parameters.isEmpty()) {
            return null
        }

        val mixPathIndex = parameters.indexOfFirst { isMixExecutable(it) }
        if (mixPathIndex == -1) {
            return null
        }

        val extraIndex = parameters.indexOf("-extra").takeIf { it >= 0 }
        if (extraIndex == null || mixPathIndex <= extraIndex) {
            return null
        }
        val mixArguments = if (mixPathIndex + 1 < parameters.size) {
            parameters.subList(mixPathIndex + 1, parameters.size)
        } else {
            emptyList()
        }

        return MixInvocation(mixArguments = mixArguments)
    }

    private fun isMixExecutable(token: String): Boolean =
        File(token).name.lowercase() in MIX_EXECUTABLES

    private fun extractTasks(mixArguments: List<String>): List<String> {
        if (mixArguments.isEmpty()) {
            return emptyList()
        }

        val tasks = mutableListOf<String>()
        val first = mixArguments.first()

        if (first == "do") {
            for (token in mixArguments.drop(1)) {
                if (token.startsWith("-")) {
                    continue
                }
                val trimmed = token.trimEnd(',')
                if (trimmed.isNotBlank()) {
                    tasks.add(trimmed)
                }
            }
        } else if (!first.startsWith("-")) {
            val trimmed = first.trimEnd(',')
            if (trimmed.isNotBlank()) {
                tasks.add(trimmed)
            }
        }

        return tasks
    }

    private fun buildMixToolingBootstrapCommandLine(
        source: GeneralCommandLine,
        project: Project?,
        sdk: Sdk,
        task: String
    ): GeneralCommandLine {
        val workingDirectory = source.workDirectory?.path
        val mixToolingBootstrapEnvironment = mutableMapOf<String, String>()
        val mixToolingBoostrap = Mix.commandLine(project, mixToolingBootstrapEnvironment, workingDirectory, sdk)
        mixToolingBoostrap.withCharset(source.charset)
        mixToolingBoostrap.withParentEnvironmentType(source.parentEnvironmentType)
        mixToolingBoostrap.withRedirectErrorStream(source.isRedirectErrorStream)
        mixToolingBoostrap.addParameter(task)
        mixToolingBoostrap.addParameters(MixToolingBootstrapSpec.ARGS)
        return mixToolingBoostrap
    }

    @Throws(ExecutionException::class)
    private fun runMixToolingBootstrapTask(
        commandLine: GeneralCommandLine,
        task: String,
        project: Project?,
        console: ConsoleView?
    ) {
        printToConsole(console, "Mix Tooling Bootstrap: mix $task\n", ConsoleViewContentType.SYSTEM_OUTPUT)
        val handler = CapturingProcessHandler(commandLine)
        if (console != null) {
            handler.addProcessListener(object : ProcessListener {
                override fun startNotified(event: ProcessEvent) {}

                override fun processTerminated(event: ProcessEvent) {}

                override fun processWillTerminate(event: ProcessEvent, willBeDestroyed: Boolean) {}

                override fun onTextAvailable(event: ProcessEvent, outputType: com.intellij.openapi.util.Key<*>) {
                    val contentType = when (outputType) {
                        ProcessOutputTypes.STDERR, ProcessOutputType.STDERR -> ConsoleViewContentType.ERROR_OUTPUT
                        else -> ConsoleViewContentType.NORMAL_OUTPUT
                    }
                    printToConsole(console, event.text, contentType)
                }
            })
        }

        val output = handler.runProcess()
        if (output.isTimeout || output.isCancelled || output.exitCode != 0) {
            val message = failureMessage(task, commandLine, output)
            logger.warn(message)
            throw ExecutionException(message)
        }

        logOutput(task, output, project)
    }

    private fun logOutput(task: String, output: ProcessOutput, project: Project?) {
        val stdout = output.stdout.trim()
        val stderr = output.stderr.trim()
        if (stdout.isNotEmpty()) {
            logger.info("mix $task stdout:\n$stdout")
        }
        if (stderr.isNotEmpty()) {
            logger.warn("mix $task stderr:\n$stderr")
        }
        if (project == null && (stdout.isNotEmpty() || stderr.isNotEmpty())) {
            logger.info("mix $task ran without a project context")
        }
    }

    private fun failureMessage(
        task: String,
        commandLine: GeneralCommandLine,
        output: ProcessOutput
    ): String {
        val message = StringBuilder()
        message.append("mix ").append(task).append(" failed")
        if (output.exitCode != 0) {
            message.append(" (exit code ").append(output.exitCode).append(')')
        }
        if (output.isTimeout) {
            message.append(" (timed out)")
        }
        if (output.isCancelled) {
            message.append(" (cancelled)")
        }
        message.append("\nCommand line: ").append(commandLine.commandLineString)

        val stdout = output.stdout.trim()
        if (stdout.isNotEmpty()) {
            message.append("\nSTDOUT:\n").append(stdout)
        }
        val stderr = output.stderr.trim()
        if (stderr.isNotEmpty()) {
            message.append("\nSTDERR:\n").append(stderr)
        }
        return message.toString()
    }

    private fun printToConsole(console: ConsoleView?, text: String, contentType: ConsoleViewContentType) {
        if (console == null) {
            return
        }

        val application = ApplicationManager.getApplication()
        if (application == null || application.isDispatchThread) {
            console.print(text, contentType)
        } else {
            application.invokeLater({ console.print(text, contentType) }, ModalityState.any())
        }
    }

    private data class MixInvocation(
        val mixArguments: List<String>
    )

    private fun isEnabled(): Boolean = ElixirExperimentalSettings.instance.state.enableMixMixToolingBootstrap
}
