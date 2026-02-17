package org.elixir_lang.mix.runner

import com.intellij.execution.ExecutionException
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessListener
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.util.Key
import com.intellij.platform.ide.progress.ModalTaskOwner
import com.intellij.platform.ide.progress.runWithModalProgressBlocking
import org.elixir_lang.Mix

private val LOG = logger<MixTaskRunner>()

/**
 * Shared utility for running Mix tasks with progress indication.
 *
 * Extracts common logic from import wizard and actions to eliminate code duplication.
 * Supports both project context (for actions) and null project (for import wizard).
 */
object MixTaskRunner {
    /**
     * Runs a single mix task with modal progress dialog.
     *
     * @param project The project context (null if running during import wizard)
     * @param workingDirectory The directory to run the mix command in
     * @param sdk The Elixir SDK to use
     * @param title The progress dialog title
     * @param task The mix task name (e.g., "deps.get", "local.hex")
     * @param taskParameters Additional parameters for the task
     * @return Result indicating success or failure with exception
     */
    fun runMixTask(
        project: Project?,
        workingDirectory: String,
        sdk: Sdk,
        title: String,
        task: String,
        vararg taskParameters: String
    ): Result<Unit> {
        return try {
            if (project != null) {
                // Modern Kotlin API for actions with project context
                runWithModalProgressBlocking(ModalTaskOwner.project(project), title) {
                    executeMixTask(workingDirectory, sdk, task, project, *taskParameters)
                }
            } else {
                // Legacy Java API for import wizard (no project context yet)
                var exception: ExecutionException? = null
                ProgressManager.getInstance().run(
                    object : Task.Modal(null, title, true) {
                        override fun run(indicator: com.intellij.openapi.progress.ProgressIndicator) {
                            try {
                                executeMixTask(workingDirectory, sdk, task, project, *taskParameters)
                            } catch (e: ExecutionException) {
                                exception = e
                            }
                        }
                    }
                )
                exception?.let { throw it }
            }
            Result.success(Unit)
        } catch (e: ExecutionException) {
            LOG.warn("Mix task '$task' failed", e)
            Result.failure(e)
        }
    }

    /**
     * Core execution logic shared by both modern and legacy APIs.
     */
    private fun executeMixTask(
        workingDirectory: String,
        sdk: Sdk,
        task: String,
        project: Project?,
        vararg taskParameters: String
    ) {
        val indicator = ProgressManager.getInstance().progressIndicator
        indicator?.isIndeterminate = true

        val commandLine = Mix.commandLine(
            emptyMap(),
            workingDirectory,
            sdk,
            emptyList(),
            emptyList(),
            project = project,
        )
        commandLine.addParameter(task)
        commandLine.addParameters(*taskParameters)

        // Use ColoredProcessHandler to automatically strip ANSI escape codes
        val handler = ProcessHandlerFactory.getInstance()
            .createColoredProcessHandler(commandLine)

        handler.addProcessListener(
            object : ProcessListener {
                override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                    val text = event.text
                    indicator?.text2 = text
                }
            }
        )

        ProcessTerminatedListener.attach(handler)
        handler.startNotify()
        handler.waitFor()

        indicator?.text2 = "Refreshing"
    }

    /**
     * Updates hex package manager.
     * Errors are logged but not thrown.
     */
    @JvmStatic
    fun updateHex(project: Project?, workingDirectory: String, sdk: Sdk) {
        runMixTask(project, workingDirectory, sdk, "Updating hex", "local.hex", "--force")
        // Result is ignored - errors are logged internally
    }

    /**
     * Updates rebar build tool.
     * Errors are logged but not thrown.
     */
    @JvmStatic
    fun updateRebar(project: Project?, workingDirectory: String, sdk: Sdk) {
        runMixTask(project, workingDirectory, sdk, "Updating rebar", "local.rebar", "--force")
        // Result is ignored - errors are logged internally
    }

    /**
     * Fetches Mix dependencies.
     * Errors are logged but not thrown.
     */
    @JvmStatic
    fun fetchDependencies(project: Project?, workingDirectory: String, sdk: Sdk) {
        runMixTask(project, workingDirectory, sdk, "Fetching dependencies", "deps.get")
        // Result is ignored - errors are logged internally
    }

    /**
     * Installs hex, rebar, and fetches dependencies (all-in-one).
     * Returns Result for Kotlin callers who want to handle errors.
     */
    fun installDependencies(project: Project, workingDirectory: String, sdk: Sdk): Result<Unit> {
        val hexResult = runMixTask(project, workingDirectory, sdk, "Updating hex", "local.hex", "--force")
        val rebarResult = runMixTask(project, workingDirectory, sdk, "Updating rebar", "local.rebar", "--force")
        val depsResult = runMixTask(project, workingDirectory, sdk, "Fetching dependencies", "deps.get")

        return hexResult
            .mapCatching { rebarResult.getOrThrow() }
            .mapCatching { depsResult.getOrThrow() }
    }
}
