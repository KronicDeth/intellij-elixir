package org.elixir_lang.notification.mix_deps

import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.currentThreadCoroutineScope
import kotlinx.coroutines.launch
import org.elixir_lang.mix.DepsCheckerService
import org.elixir_lang.mix.createMixDepsStatusRunConfiguration
import org.elixir_lang.mix.runner.runMixRunConfiguration
import org.elixir_lang.notification.setup_sdk.Notifier
import com.intellij.openapi.vfs.VirtualFile

class ShowStatusAction(private val rootHint: VirtualFile? = null) : NotificationAction("Run mix deps") {
    override fun actionPerformed(e: AnActionEvent, notification: Notification) {
        val project = e.project
        if (project != null) {
            Notifier.clearMixDepsOutdated(project)
        } else {
            notification.expire()
            return
        }

        val fileHint = rootHint ?: e.getData(CommonDataKeys.VIRTUAL_FILE)

        currentThreadCoroutineScope().launch {
            runMixRunConfiguration(
                project = project,
                fileHint = fileHint,
                createSettings = ::createMixDepsStatusRunConfiguration,
                listenerDisposableName = "mix-deps-status-listener",
                onNoRoot = {
                    Notifier.mixDepsError(project, project.name, "Could not determine project root directory")
                },
                onNoSdk = { rootName ->
                    Notifier.mixDepsNoSdk(project, rootName)
                },
                onNoModule = { rootName, rootPath ->
                    Notifier.mixDepsError(project, rootName, "Could not determine module for $rootPath")
                },
                onProcessTerminated = { _, _ ->
                    project.service<DepsCheckerService>().scheduleCheckNow("mix deps status completed")
                },
                onException = { rootName, message ->
                    Notifier.mixDepsError(project, rootName, message)
                }
            )
        }
    }
}
