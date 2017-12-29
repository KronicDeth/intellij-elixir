package org.elixir_lang.utils

import com.intellij.notification.Notification
import com.intellij.notification.NotificationListener
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import org.elixir_lang.inspection.SetupSDKNotificationProvider.Companion.showFacetSettings
import org.elixir_lang.inspection.SetupSDKNotificationProvider.Companion.showModuleSettings
import org.elixir_lang.inspection.SetupSDKNotificationProvider.Companion.showProjectSettings
import org.elixir_lang.inspection.SetupSDKNotificationProvider.Companion.showSmallIDEFacetSettings
import org.elixir_lang.sdk.ProcessOutput
import java.io.File
import javax.swing.event.HyperlinkEvent

/**
 * Created by zyuyou on 15/7/8.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/utils/ErlangExternalToolsNotificationListener.java
 */
class SetupElixirSDKNotificationListener(private val project: Project, private val file: File) : NotificationListener {
    override fun hyperlinkUpdate(notification: Notification, event: HyperlinkEvent) {
        if (event.eventType == HyperlinkEvent.EventType.ACTIVATED) {
            if (event.description == "configure" && !project.isDisposed) {
                LocalFileSystem.getInstance().findFileByIoFile(file)?.let {
                    val module = ModuleUtilCore.findModuleForFile(it, project)

                    if (module != null) {
                        if (module.getOptionValue(Module.ELEMENT_TYPE) == "ELIXIR_MODULE") {
                            showModuleSettings(project, module)
                        } else {
                            showFacetSettings(project)
                        }
                    } else {
                        if (ProcessOutput.isSmallIde()) {
                            showSmallIDEFacetSettings(project)
                        } else {
                            showProjectSettings(project)
                        }
                    }
                }
            }
        }
    }
}
