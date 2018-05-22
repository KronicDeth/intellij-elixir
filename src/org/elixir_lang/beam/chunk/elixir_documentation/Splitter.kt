package org.elixir_lang.beam.chunk.elixir_documentation

import com.intellij.openapi.project.Project
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTabbedPane
import org.elixir_lang.beam.chunk.ElixirDocumentation
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class Splitter(
        private val elixirDocumentation: ElixirDocumentation,
        private val project: Project,
        private val moduleName: String?,
        tabbedPane: JBTabbedPane
): OnePixelSplitter(false), ChangeListener {
    init {
        tabbedPane.addChangeListener(this)
    }

    override fun stateChanged(changeEvent: ChangeEvent) {
        if (changeEvent.source.let { it as JBTabbedPane }.selectedComponent == this) {
            ensureChildrenAdded()
        }
    }

    private var childrenAdded = false

    private fun addChildren() {
        val tree = Tree(moduleName, Model(elixirDocumentation))

        firstComponent = JBScrollPane(tree)
        secondComponent = Panel(tree, project, moduleName)
    }

    private fun ensureChildrenAdded() {
        if (!childrenAdded) {
            addChildren()
            childrenAdded = true
        }
    }
}
