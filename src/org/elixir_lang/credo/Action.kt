package org.elixir_lang.credo

import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project

/**
 * Created by zyuyou on 15/7/8.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/utils/ErlangExternalToolsNotificationListener.java
 */
class Action(private val project: Project) : NotificationAction("Configure credo") {
    override fun actionPerformed(e: AnActionEvent, notification: Notification) {
        if (project.isDisposed) return

        ShowSettingsUtil.getInstance().showSettingsDialog(project, Configurable::class.java)
    }
}
