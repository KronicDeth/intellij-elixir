package org.elixir_lang.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.elixir_lang.sdk.elixir.ProjectStructureSdkSettingsOpener
import org.elixir_lang.sdk.elixir.SdkSettingsOpener

class OpenElixirProjectStructureSdksAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        SdkSettingsOpener.getInstance().open(e)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = SdkSettingsOpener.getInstance() is ProjectStructureSdkSettingsOpener
    }

    override fun isDumbAware(): Boolean = true

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
