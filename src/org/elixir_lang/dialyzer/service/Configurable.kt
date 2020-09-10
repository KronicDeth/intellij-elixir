package org.elixir_lang.dialyzer.service

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.Nls
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class Configurable(project: Project) : SearchableConfigurable {
    private val service: DialyzerService = project.getService(DialyzerService::class.java)
    private var mixDialyzerCommand: JTextField? = null
    private var panel: JPanel? = null
    override fun getId() = "Dialyzer"

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return id
    }

    override fun createComponent(): JComponent? {
        return panel
    }

    override fun isModified(): Boolean {
        return service.mixDialyzerCommand != mixDialyzerCommand?.text
    }

    override fun apply() {
        val textField = mixDialyzerCommand
        if (textField?.text != null) {
            service.mixDialyzerCommand = textField.text
        }
    }

    override fun reset() {
        mixDialyzerCommand?.text = service.mixDialyzerCommand
    }
}
