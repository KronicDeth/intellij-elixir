package org.elixir_lang.create_module

import com.intellij.ide.actions.CreateFromTemplateAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.application.WriteActionAware
import com.intellij.openapi.ui.DialogWrapper.OK_EXIT_CODE
import org.elixir_lang.Icons

class Action : AnAction(NEW_ELIXIR_MODULE, DESCRIPTION, Icons.FILE), WriteActionAware {
    override fun equals(other: Any?): Boolean = other is Action
    override fun hashCode(): Int = 0
    override fun isDumbAware(): Boolean = true

    /**
     * See [CreateFromTemplateAction.actionPerformed]
     */
    override fun actionPerformed(event: AnActionEvent) {
        val dataContext = event.dataContext

        LangDataKeys.IDE_VIEW.getData(dataContext)?.let { view ->
            CommonDataKeys.PROJECT.getData(dataContext)?.let { project ->
                view.orChooseDirectory?.let { directory ->
                    val dialog = Dialog(project, directory)

                    dialog.show()

                    if (dialog.exitCode == OK_EXIT_CODE) {
                        dialog.created?.let { createdElement ->
                            view.selectElement(createdElement)
                        }
                    }
                }
            }
        }
    }
}
