package org.elixir_lang.formatter.settings

import com.intellij.application.options.codeStyle.OptionTreeWithPreviewPanel
import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.util.NlsContexts.TabTitle
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider.SettingsType
import org.elixir_lang.ElixirFileType
import org.elixir_lang.ElixirLanguage

// Based on [JavaDocFormattingPanel](https://github.com/JetBrains/intellij-community/blob/f3f09fc61977c612ac83ccba2233d0708745e12a/java/java-impl/src/com/intellij/application/options/JavaDocFormattingPanel.java)
class MixFormatPanel(settings: CodeStyleSettings?) : OptionTreeWithPreviewPanel(settings) {
    init {
        init()
    }

    override fun getSettingsType(): SettingsType = SettingsType.LANGUAGE_SPECIFIC

    override fun initTables() {
        initCustomOptions("General")
    }

    override fun getRightMargin(): Int = 47

    override fun getPreviewText(): String = LanguageCodeStyleSettingsProvider.forLanguage(ElixirLanguage)!!
        .getCodeSample(SettingsType.LANGUAGE_SPECIFIC)!!

    override fun getFileType(): FileType = ElixirFileType.INSTANCE

    override fun customizeSettings() {
        LanguageCodeStyleSettingsProvider.forLanguage(ElixirLanguage)!!.customizeSettings(this, settingsType)
    }

    override fun getTabTitle(): @TabTitle String = "mix format"
    override fun getDefaultLanguage(): Language = ElixirLanguage
}
