package org.elixir_lang.notification.mix_deps

import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import org.elixir_lang.action.InstallMixDependenciesAction
import org.elixir_lang.notification.setup_sdk.Notifier

class InstallAction() : NotificationAction("Install deps") {
    override fun actionPerformed(e: AnActionEvent, notification: Notification) {
        val project = e.project
        if (project != null) {
            Notifier.clearMixDepsOutdated(project)
        } else {
            notification.expire()
        }
        val action = ActionManager.getInstance().getAction("Elixir.InstallMixDependencies")
        if (action is InstallMixDependenciesAction ) {
            action.actionPerformed(e)
        }
    }
}
