package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1

import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1
import org.elixir_lang.beam.chunk.inspect
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.HORIZONTAL
import java.awt.GridBagConstraints.LINE_START
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

/**
 * Panel with all single-value attributes from [org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1]
 */
class SingletonPanel(debugInfo: V1): JPanel(GridBagLayout()) {
    init {
        val shrink = Double.MIN_VALUE
        val expand = 1.0 - shrink
        val fileTextField = JTextField(debugInfo.file).apply {
            isEditable = false
        }
        val fileLabel = JLabel("File:").apply {
            labelFor = fileTextField
        }
        add(
                fileLabel,
                GridBagConstraints().apply {
                    anchor = LINE_START
                    gridx = 0; gridy = 0
                    weightx = shrink
                }
        )
        add(
                fileTextField,
                GridBagConstraints().apply {
                    fill = HORIZONTAL
                    gridx = 1; gridy = 0
                    weightx = expand
                }
        )

        val lineTextField = JTextField(debugInfo.line?.toString()).apply {
            isEditable = false
        }
        val lineLabel = JLabel("Line:").apply {
            labelFor = lineTextField
        }
        add(
                lineLabel,
                GridBagConstraints().apply {
                    anchor = LINE_START
                    gridx = 0; gridy = 1;
                    weightx = shrink
                }
        )
        add(
                lineTextField,
                GridBagConstraints().apply {
                    anchor = LINE_START
                    fill = GridBagConstraints.HORIZONTAL
                    gridx = 1; gridy = 1;
                    weightx = expand
                }
        )

        val moduleTextField = JTextField(debugInfo.module?.let { inspect(it) }).apply {
            isEditable = false
        }
        val moduleLabel = JLabel("Module:").apply {
            labelFor = moduleTextField
        }
        add(
                moduleLabel,
                GridBagConstraints().apply {
                    anchor = LINE_START
                    gridx = 0; gridy = 2
                    weightx = shrink
                }
        )
        add(
                moduleTextField,
                GridBagConstraints().apply {
                    fill = GridBagConstraints.HORIZONTAL
                    gridx = 1; gridy = 2;
                    weightx = expand
                }
        )
    }
}
