package org.elixir_lang.action

import com.intellij.ide.BrowserUtil
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.extensions.PluginId

class HelpAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val version =
                PluginId
                        .findId("org.elixir_lang")!!
                        .let { PluginManagerCore.getPlugin(it)!! }
                        .version

        BrowserUtil.browse("https://github.com/KronicDeth/intellij-elixir/blob/v$version/README.md")
    }

    override fun isDumbAware(): Boolean = true
}
