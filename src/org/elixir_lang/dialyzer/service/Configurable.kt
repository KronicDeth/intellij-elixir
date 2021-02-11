package org.elixir_lang.dialyzer.service

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.Nls
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class Configurable(project: Project) : SearchableConfigurable {
    private val service: DialyzerService = project.getService(DialyzerService::class.java)
    private var mixArguments: JTextField? = null
    private var elixirArguments: JTextField? = null
    private var erlArguments: JTextField? = null
    private var panel: JPanel? = null
    override fun getId() = "Dialyzer"

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String = id

    override fun createComponent(): JComponent? = panel

    override fun isModified(): Boolean =
            (service.mixArguments != mixArguments?.text) ||
            (service.elixirArguments != elixirArguments?.text) ||
            (service.erlArguments != erlArguments?.text)

    override fun apply() {
        mixArguments?.text?.let {
            service.mixArguments = it
        }
        elixirArguments?.text?.let {
            service.elixirArguments = it
        }
        erlArguments?.text?.let {
            service.erlArguments = it
        }
    }

    override fun reset() {
        mixArguments?.text = service.mixArguments
        elixirArguments?.text = service.elixirArguments
        erlArguments?.text = service.erlArguments
    }
}
