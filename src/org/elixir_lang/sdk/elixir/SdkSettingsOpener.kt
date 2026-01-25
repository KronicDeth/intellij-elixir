package org.elixir_lang.sdk.elixir

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.ProjectManager

interface SdkSettingsOpener {
    fun open(event: AnActionEvent)

    fun targetName(): String

    companion object {
        fun getInstance(): SdkSettingsOpener =
            ApplicationManager.getApplication().getService(SdkSettingsOpener::class.java)
    }
}

class SettingsSdkSettingsOpener : SdkSettingsOpener {
    override fun open(event: AnActionEvent) {
        val project = event.project ?: ProjectManager.getInstance().openProjects.firstOrNull()
        ShowSettingsUtil.getInstance().showSettingsDialog(project, org.elixir_lang.facet.sdks.elixir.Configurable::class.java)
    }

    override fun targetName(): String = "Settings"
}

class ProjectStructureSdkSettingsOpener : SdkSettingsOpener {
    override fun open(event: AnActionEvent) {
        val action = ActionManager.getInstance().getAction("ShowProjectStructureSettings")
        if (action != null) {
            ActionUtil.performAction(action, event)
        }
    }

    override fun targetName(): String = "Project Structure"
}
