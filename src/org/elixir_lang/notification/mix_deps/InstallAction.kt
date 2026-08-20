package org.elixir_lang.notification.mix_deps

import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionUiKind
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.notification.setup_sdk.Notifier

class InstallAction(private val rootHint: VirtualFile? = null) : NotificationAction("Install deps") {
    override fun actionPerformed(e: AnActionEvent, notification: Notification) {
        val action = ActionManager.getInstance().getAction("Elixir.InstallMixDependencies")
        if (action != null) {
            val eventToUse = if (rootHint != null) {
                val dataContext = SimpleDataContext.builder()
                    .setParent(e.dataContext)
                    .add(CommonDataKeys.VIRTUAL_FILE, rootHint)
                    .build()
                AnActionEvent.createEvent(dataContext, e.presentation.clone(), e.place, ActionUiKind.NONE, null)
            } else {
                e
            }
            ActionUtil.performAction(action, eventToUse)
        }
        val project = e.project
        if (project != null) {
            Notifier.clearMixDepsOutdated(project)
        } else {
            notification.expire()
        }
    }
}
