package org.elixir_lang.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.currentThreadCoroutineScope
import kotlinx.coroutines.launch
import org.elixir_lang.mix.DepsCheckerService
import org.elixir_lang.mix.createInstallMixDependenciesRunConfiguration
import org.elixir_lang.mix.runner.runMixRunConfiguration
import org.elixir_lang.notification.setup_sdk.Notifier

class InstallMixDependenciesAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val fileHint = e.getData(CommonDataKeys.VIRTUAL_FILE)

        currentThreadCoroutineScope().launch {
            runMixRunConfiguration(
                project = project,
                fileHint = fileHint,
                createSettings = ::createInstallMixDependenciesRunConfiguration,
                listenerDisposableName = "mix-deps-install-listener",
                onNoRoot = {
                    Notifier.mixDepsError(project, project.name, "Could not determine project root directory")
                },
                onNoSdk = { rootName ->
                    Notifier.mixDepsNoSdk(project, rootName)
                },
                onNoModule = { rootName, rootPath ->
                    Notifier.mixDepsError(project, rootName, "Could not determine module for $rootPath")
                },
                onProcessTerminated = { exitCode, rootName ->
                    project.service<DepsCheckerService>().scheduleCheckNow("mix deps install completed")
                    if (exitCode == 0) {
                        Notifier.mixDepsInstallSuccess(project, rootName)
                    } else {
                        Notifier.mixDepsInstallError(project, rootName, "Non-zero exit code")
                    }
                },
                onException = { rootName, message ->
                    Notifier.mixDepsInstallError(project, rootName, message)
                }
            )
        }
    }

    override fun isDumbAware(): Boolean = true
}
