package org.elixir_lang.beam.chunk.code

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.ui.TabbedPaneWrapper
import com.intellij.ui.tabs.impl.JBEditorTabs
import com.intellij.util.ui.components.BorderLayoutPanel
import org.elixir_lang.beam.Cache
import org.elixir_lang.beam.assembly.Controls
import org.elixir_lang.beam.assembly.file.Type
import javax.swing.JComponent
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class Component(private val cache: Cache, private val project: Project, tabbedPane: TabbedPaneWrapper) :
        BorderLayoutPanel(), ChangeListener {
    init {
        tabbedPane.addChangeListener(this)
    }

    override fun stateChanged(changeEvent: ChangeEvent) {
        if (changeEvent.source.let { it as JBEditorTabs }.selectedInfo?.component == parent) {
            ensureChildrenAdded()
        }
    }

    private var childrenAdded = false

    private fun addChildren() {
        val controls = Controls(cache, project)
        addToTop(controls)

        val document = controls.document
        val editorComponent = editorComponent(document, project)
        addToCenter(editorComponent)
    }

    private fun ensureChildrenAdded() {
        if (!childrenAdded) {
            addChildren()
            childrenAdded = true
        }
    }
}

private fun editorComponent(document: Document, project: Project): JComponent =
        EditorFactory.getInstance().createEditor(document, project, Type, true).component
