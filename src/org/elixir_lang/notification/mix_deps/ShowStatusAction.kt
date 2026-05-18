package org.elixir_lang.notification.mix_deps

import com.intellij.execution.ExecutionListener
import com.intellij.execution.ExecutionManager
import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.mix.DepsCheckerService
import org.elixir_lang.mix.createMixDepsStatusRunConfiguration
import org.elixir_lang.notification.setup_sdk.Notifier
import org.elixir_lang.util.ElixirProjectDisposable
import org.elixir_lang.sdk.elixir.findElixirSdkForRoot

class ShowStatusAction : NotificationAction("Run mix deps") {
    override fun actionPerformed(e: AnActionEvent, notification: Notification) {
        val project = e.project
        if (project != null) {
            Notifier.clearMixDepsOutdated(project)
        } else {
            notification.expire()
            return
        }

        val projectRoot = getProjectRoot(project)
        if (projectRoot == null) {
            Notifier.mixDepsError(project, project.name, "Could not determine project root directory")
            return
        }

        val sdk = findElixirSdkForRoot(project, projectRoot)
        if (sdk == null) {
            Notifier.mixDepsNoSdk(project, projectRoot.name)
            return
        }

        try {
            val settings = createMixDepsStatusRunConfiguration(project, projectRoot)
            if (settings == null) {
                Notifier.mixDepsError(project, projectRoot.name, "Could not determine module for ${projectRoot.path}")
                return
            }

            val runManager = RunManager.getInstance(project)
            runManager.setTemporaryConfiguration(settings)
            runManager.selectedConfiguration = settings

            val listenerDisposable = Disposer.newDisposable("mix-deps-status-listener")
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
                                .scheduleCheckNow("mix deps status completed")
                            Disposer.dispose(listenerDisposable)
                        }
                    }
                }
            )

            ProgramRunnerUtil.executeConfiguration(settings, DefaultRunExecutor.getRunExecutorInstance())
        } catch (e: Exception) {
            Notifier.mixDepsError(project, projectRoot.name, e.message ?: "Unknown error")
        }
    }

    private fun getProjectRoot(project: Project): VirtualFile? {
        val contentRoots = ProjectRootManager.getInstance(project).contentRootsFromAllModules

        return contentRoots.firstOrNull { root ->
            root.findChild("mix.exs") != null
        } ?: contentRoots.firstOrNull()
    }
}
