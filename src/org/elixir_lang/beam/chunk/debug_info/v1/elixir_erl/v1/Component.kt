package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1

import com.intellij.openapi.project.Project
import com.intellij.ui.TabbedPaneWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.tabs.impl.JBEditorTabs
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.BOTH
import java.awt.GridBagConstraints.HORIZONTAL
import java.awt.GridBagLayout
import javax.swing.JPanel
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class Component(private val debugInfo: V1, private val project: Project, tabbedPane: TabbedPaneWrapper) : JPanel(GridBagLayout()), ChangeListener {
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
        val shrink = Double.MIN_VALUE
        val expand = 1.0 - shrink

        add(
                JBScrollPane(SingletonPanel(debugInfo)),
                GridBagConstraints().apply {
                    fill = HORIZONTAL
                    gridy = 0
                    weightx = 1.0; weighty = shrink
                }
        )
        add(
                MultipleTabbedPane(debugInfo, project),
                GridBagConstraints().apply {
                    fill = BOTH
                    gridy = 1
                    weightx = 1.0; weighty = expand
                }
        )
    }

    private fun ensureChildrenAdded() {
        if (!childrenAdded) {
            addChildren()
            childrenAdded = true
        }
    }
}
