package org.elixir_lang

import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.ide.wizard.language.LanguageGeneratorNewProjectWizard
import org.elixir_lang.new_project_wizard.Step
import javax.swing.Icon

class NewProjectWizard : LanguageGeneratorNewProjectWizard {
    override val name: String = "Elixir"
    override val ordinal: Int = Int.MAX_VALUE
    override val icon: Icon = Icons.LANGUAGE

    override fun createStep(parent: NewProjectWizardStep): NewProjectWizardStep =
        Step(parent)
}
