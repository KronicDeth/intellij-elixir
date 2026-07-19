package org.elixir_lang.action

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.elixir_lang.pluginVersion

class HelpAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val version = pluginVersion()

        BrowserUtil.browse("https://github.com/KronicDeth/intellij-elixir/blob/v$version/README.md")
    }

    override fun isDumbAware(): Boolean = true
}
