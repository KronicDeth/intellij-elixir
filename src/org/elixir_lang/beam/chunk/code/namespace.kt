package org.elixir_lang.beam.chunk.code

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.util.ui.components.BorderLayoutPanel
import org.elixir_lang.beam.Cache
import org.elixir_lang.beam.assembly.Controls
import org.elixir_lang.beam.assembly.file.Type
import javax.swing.JComponent


fun component(cache: Cache, project: Project): JComponent {
    val controls = Controls(cache, project)
    val document = controls.document
    val editorComponent = editorComponent(document, project)

    return BorderLayoutPanel().addToTop(controls).addToCenter(editorComponent)
}

// Private functions

private fun editorComponent(document: Document, project: Project): JComponent =
    EditorFactory.getInstance().createEditor(document, project, Type, true).component
