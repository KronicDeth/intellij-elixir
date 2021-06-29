package org.elixir_lang.notification.setup_sdk

import com.intellij.notification.Notification
import com.intellij.notification.NotificationListener
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import org.elixir_lang.notification.setup_sdk.Provider.Companion.showFacetSettings
import org.elixir_lang.notification.setup_sdk.Provider.Companion.showModuleSettings
import javax.swing.event.HyperlinkEvent

/**
 * Created by zyuyou on 15/7/8.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/utils/ErlangExternalToolsNotificationListener.java
 */
class Listener(private val project: Project, private val module: Module) : NotificationListener {
    override fun hyperlinkUpdate(notification: Notification, event: HyperlinkEvent) {
        if (event.eventType == HyperlinkEvent.EventType.ACTIVATED) {
            if (event.description == "configure" && !project.isDisposed && !module.isDisposed) {
                if (module.moduleTypeName == "ELIXIR_MODULE") {
                    showModuleSettings(project, module)
                } else {
                    showFacetSettings(project)
                }
            }
        }
    }
}
