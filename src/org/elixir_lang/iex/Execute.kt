package org.elixir_lang.iex

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.ex.EditorEx
import org.elixir_lang.iex.console.view.Registry

class Execute : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        event.getData(CommonDataKeys.EDITOR)?.let { Registry[it] }?.execute()
    }

    override fun update(event: AnActionEvent) {
        val presentation = event.presentation
        val editor = event.getData(CommonDataKeys.EDITOR)

        if (editor !is EditorEx || editor.isRendererMode) {
            presentation.isEnabled = false
        } else {
            val consoleView = Registry[editor]

            if (consoleView == null) {
                presentation.isEnabled = false
            } else {
                presentation.isEnabledAndVisible = consoleView.isRunning
            }
        }
    }
}
