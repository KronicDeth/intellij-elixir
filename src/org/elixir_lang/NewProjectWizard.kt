package org.elixir_lang

import com.intellij.ide.wizard.LanguageNewProjectWizard
import com.intellij.ide.wizard.NewProjectWizardLanguageStep
import com.intellij.ide.wizard.NewProjectWizardStep
import org.elixir_lang.new_project_wizard.Step

class NewProjectWizard : LanguageNewProjectWizard {
    override val name: String = "Elixir"
    override val ordinal: Int = Int.MAX_VALUE

    override fun createStep(parent: NewProjectWizardLanguageStep): NewProjectWizardStep =
        Step(parent)
}
