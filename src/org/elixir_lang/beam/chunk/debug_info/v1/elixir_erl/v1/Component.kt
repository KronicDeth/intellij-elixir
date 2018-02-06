package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.BOTH
import java.awt.GridBagConstraints.HORIZONTAL
import java.awt.GridBagLayout
import javax.swing.JPanel

class Component(debugInfo: V1, project: Project) : JPanel(GridBagLayout()) {
    init {
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
}
