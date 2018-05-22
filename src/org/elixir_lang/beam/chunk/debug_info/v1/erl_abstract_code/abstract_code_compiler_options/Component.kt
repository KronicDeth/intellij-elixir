package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTabbedPane
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.AbstractCodeCompileOptions
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Splitter
import javax.swing.JPanel
import javax.swing.JTabbedPane
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class Component(
        private val debugInfo: AbstractCodeCompileOptions,
        private val project: Project,
        tabbedPane: JBTabbedPane
):
        JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT), ChangeListener {

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
        addTab("Abstract Code", Splitter(debugInfo, project))
        addTab("Compile Options", JPanel())
    }

    private fun ensureChildrenAdded() {
        if (!childrenAdded) {
            addChildren()
            childrenAdded = true
        }
    }
}
