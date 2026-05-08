package org.elixir_lang.action

import com.intellij.execution.ExecutionListener
import com.intellij.execution.ExecutionManager
import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.util.Disposer
import org.elixir_lang.mix.createInstallMixDependenciesRunConfiguration
import org.elixir_lang.mix.DepsCheckerService
import org.elixir_lang.notification.setup_sdk.Notifier
import org.elixir_lang.util.ElixirProjectDisposable
import org.elixir_lang.sdk.elixir.findElixirSdkForRoot

class InstallMixDependenciesAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val projectRoot = getProjectRoot(project)
        if (projectRoot == null) {
            Notifier.mixDepsError(project, "Could not determine project root directory")
            return
        }

        val sdk = findElixirSdkForRoot(project, projectRoot)
        if (sdk == null) {
            Notifier.mixDepsNoSdk(project)
            return
        }

        try {
            val settings = createInstallMixDependenciesRunConfiguration(project, projectRoot)
            if (settings == null) {
                Notifier.mixDepsError(project, "Could not determine module for ${projectRoot.path}")
                return
            }

            val runManager = RunManager.getInstance(project)
            runManager.setTemporaryConfiguration(settings)
            runManager.selectedConfiguration = settings

            val listenerDisposable = Disposer.newDisposable("mix-deps-install-listener")
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
                        project.service<DepsCheckerService>()
                            .scheduleCheckNow("mix deps install completed")
                        if (exitCode == 0) {
                            Notifier.mixDepsInstallSuccess(project)
                        } else {
                            Notifier.mixDepsInstallError(project, "Non-zero exit code")
                        }
                        Disposer.dispose(listenerDisposable)
                    }
                }
            })

            ProgramRunnerUtil.executeConfiguration(settings, DefaultRunExecutor.getRunExecutorInstance())
        } catch (e: Exception) {
            Notifier.mixDepsInstallError(project, e.message ?: "Unknown error")
        }
    }

    override fun isDumbAware(): Boolean = true

    private fun getProjectRoot(project: Project): VirtualFile? {
        val contentRoots = ProjectRootManager.getInstance(project).contentRootsFromAllModules

        // Find first root that has mix.exs
        return contentRoots.firstOrNull { root ->
            root.findChild("mix.exs") != null
        } ?: contentRoots.firstOrNull()
    }
}
