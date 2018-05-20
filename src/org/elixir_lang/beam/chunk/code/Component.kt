package org.elixir_lang.beam.chunk.code

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.util.ui.components.BorderLayoutPanel
import org.elixir_lang.beam.Cache
import org.elixir_lang.beam.assembly.Controls
import org.elixir_lang.beam.assembly.file.Type
import javax.swing.JComponent

class Component(private val cache: Cache, private val project: Project) : BorderLayoutPanel() {
    override fun addNotify() {
        val controls = Controls(cache, project)
        addToTop(controls)

        val document = controls.document
        val editorComponent = editorComponent(document, project)
        addToCenter(editorComponent)

        super.addNotify()
    }
}

private fun editorComponent(document: Document, project: Project): JComponent =
    EditorFactory.getInstance().createEditor(document, project, Type, true).component
