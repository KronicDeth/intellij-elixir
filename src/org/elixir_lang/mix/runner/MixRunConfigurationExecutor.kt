package org.elixir_lang.mix.runner

import com.intellij.execution.ExecutionListener
import com.intellij.execution.ExecutionManager
import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.application.EDT
import com.intellij.openapi.application.readAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.elixir_lang.util.ElixirProjectDisposable
import kotlin.coroutines.cancellation.CancellationException

internal suspend fun runMixRunConfiguration(
    project: Project,
    fileHint: VirtualFile?,
    createSettings: (Project, VirtualFile) -> RunnerAndConfigurationSettings?,
    listenerDisposableName: String,
    onNoRoot: () -> Unit,
    onNoSdk: (String) -> Unit,
    onNoModule: (String, String?) -> Unit,
    onProcessTerminated: (Int, String) -> Unit,
    onException: (String, String) -> Unit,
) {
    var errorRootName = project.name

    try {
        val preparation = readAction { prepareMixRunConfiguration(project, fileHint, createSettings) }

        errorRootName = preparation.rootName ?: project.name

        when (preparation.status) {
            MixRunConfigurationPreparationStatus.NO_ROOT -> {
                onNoRoot()
                return
            }

            MixRunConfigurationPreparationStatus.NO_SDK -> {
                onNoSdk(errorRootName)
                return
            }

            MixRunConfigurationPreparationStatus.NO_MODULE -> {
                onNoModule(errorRootName, preparation.rootPath)
                return
            }

            MixRunConfigurationPreparationStatus.READY -> Unit
        }

        val settings = preparation.settings ?: return
        val projectRootName = preparation.rootName ?: project.name

        withContext(Dispatchers.EDT) {
            val runManager = RunManager.getInstance(project)
            runManager.setTemporaryConfiguration(settings)
            runManager.selectedConfiguration = settings

            val listenerDisposable = Disposer.newDisposable(listenerDisposableName)
            Disposer.register(ElixirProjectDisposable.getInstance(project), listenerDisposable)
            project.messageBus.connect(listenerDisposable).subscribe(
                ExecutionManager.EXECUTION_TOPIC,
                object : ExecutionListener {
                    override fun processTerminated(
                        executorId: String,
                        env: ExecutionEnvironment,
                        handler: ProcessHandler,
                        exitCode: Int
                    ) {
                        if (env.runProfile === settings.configuration) {
                            onProcessTerminated(exitCode, projectRootName)
                            Disposer.dispose(listenerDisposable)
                        }
                    }
                }
            )

            ProgramRunnerUtil.executeConfiguration(settings, DefaultRunExecutor.getRunExecutorInstance())
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        onException(errorRootName, e.message ?: "Unknown error")
    }
}
