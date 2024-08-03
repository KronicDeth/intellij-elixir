package org.elixir_lang.formatter.settings

import com.intellij.application.options.TabbedLanguageCodeStylePanel
import com.intellij.psi.codeStyle.CodeStyleSettings
import org.elixir_lang.ElixirLanguage

class ElixirCodeStyleMainPanel(currentSettings: CodeStyleSettings?, baseSettings: CodeStyleSettings) :
    TabbedLanguageCodeStylePanel(ElixirLanguage, currentSettings, baseSettings) {

    override fun initTabs(settings: CodeStyleSettings) {
        addTab(MixFormatPanel(settings))
        addIndentOptionsTab(settings)
        addSpacesTab(settings)
        addWrappingAndBracesTab(settings)
        addTab(CodeGenerationPanel(settings))
        // DO NOT have Blank Lines tab
    }
}
