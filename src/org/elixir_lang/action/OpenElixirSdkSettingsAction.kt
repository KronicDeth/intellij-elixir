package org.elixir_lang.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.elixir_lang.sdk.elixir.SdkSettingsOpener
import org.elixir_lang.sdk.elixir.SettingsSdkSettingsOpener

class OpenElixirSdkSettingsAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        SdkSettingsOpener.getInstance().open(e)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = SdkSettingsOpener.getInstance() is SettingsSdkSettingsOpener
    }

    override fun isDumbAware(): Boolean = true

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
