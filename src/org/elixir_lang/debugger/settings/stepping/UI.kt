package org.elixir_lang.debugger.settings.stepping

import com.intellij.openapi.options.ConfigurableUi
import org.elixir_lang.debugger.Settings
import org.elixir_lang.debugger.settings.stepping.module_filter.Editor
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class UI: ConfigurableUi<Settings> {
    private lateinit var filterEditor: Editor

    override fun apply(settings: Settings) {
        filterEditor.stopEditing()

        settings.moduleFilters.apply {
            clear()
            addAll(filterEditor.filters)
        }
    }

    override fun getComponent(): JComponent =
        JPanel(BorderLayout()).apply {
            filterEditor = Editor()

            add(JLabel("Do not step into the Modules:"), BorderLayout.NORTH)
            add(filterEditor, BorderLayout.CENTER)
        }

    override fun isModified(settings: Settings)= toSettings() != settings

    override fun reset(settings: Settings) {
        filterEditor.filters = settings.moduleFilters
    }

    // Private Functions

    private fun toSettings() = Settings(filterEditor.filters)
}
