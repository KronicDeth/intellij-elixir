package org.elixir_lang.iex.console.view

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.elixir_lang.iex.console.View

object Registry {
    private val editorToView = mutableMapOf<Editor, View>()
    private val projectToView = mutableMapOf<Project, View>()

    fun add(view: View) {
        editorToView[view.consoleEditor] = view
        projectToView[view.project] = view
    }

    fun remove(view: View) {
        editorToView.remove(view.consoleEditor)
        projectToView.remove(view.project)
    }

    operator fun get(editor: Editor): View? = editorToView[editor]
    operator fun get(project: Project): View? = projectToView[project]
}
