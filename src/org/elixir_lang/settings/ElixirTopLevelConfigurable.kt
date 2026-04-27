package org.elixir_lang.settings

import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * Always-visible top-level Elixir settings page for full IDEs.
 * Child configurables (Credo, Dialyzer, SDKs, etc.) provide the editable settings.
 */
class ElixirTopLevelConfigurable : Configurable, Configurable.NoScroll {
    override fun getDisplayName(): String = "Elixir"

    override fun createComponent(): JComponent = JPanel(BorderLayout()).apply {
        border = JBUI.Borders.empty(10)
        add(JBLabel("Configure Elixir using the child pages in this section (SDKs, Credo, Dialyzer, Experimental Settings)."), BorderLayout.NORTH)
    }

    override fun isModified(): Boolean = false

    override fun apply() {
        // No mutable settings at the top-level container.
    }
}
