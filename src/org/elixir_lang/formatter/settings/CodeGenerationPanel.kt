package org.elixir_lang.formatter.settings

import com.intellij.application.options.CodeStyleAbstractPanel
import com.intellij.application.options.codeStyle.CommenterForm
import com.intellij.lang.Language
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.editor.highlighter.EditorHighlighter
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeEditorHighlighterProviders
import com.intellij.openapi.util.BuildNumber
import com.intellij.openapi.util.NlsContexts.TabTitle
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable.CommenterOption
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider.SettingsType
import org.elixir_lang.ElixirFileType
import org.elixir_lang.ElixirLanguage
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.BoxLayout
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.IdeBorderFactory
import com.intellij.util.ui.JBInsets

class CodeGenerationPanel(settings: CodeStyleSettings) : CodeStyleAbstractPanel(settings) {

    private val panel: JPanel
    private val myCommenterForm: CommenterForm;
    private var myPanel: JComponent? = null

    init {
        myCommenterForm = CommenterForm(ElixirLanguage)
        panel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = IdeBorderFactory.createEmptyBorder(JBInsets(0, 10, 10, 10))
            add(myCommenterForm.commenterPanel)
        }
    }

    override fun getFileType(): FileType = ElixirFileType.INSTANCE

    override fun getTabTitle(): @TabTitle String = "Code Generation"

    override fun getDefaultLanguage(): Language = ElixirLanguage

    override fun getRightMargin(): Int = 47

    override fun getPreviewText(): String = LanguageCodeStyleSettingsProvider.forLanguage(ElixirLanguage)!!
        .getCodeSample(SettingsType.LANGUAGE_SPECIFIC)!!

    override fun apply(settings: CodeStyleSettings) {
        if (isModified(settings)) myCommenterForm.apply(settings)
    }

    override fun isModified(settings: CodeStyleSettings): Boolean {
        return myCommenterForm.isModified(settings)
    }

    fun getPanelInner(): JComponent {
        return panel
    }

    override fun resetImpl(settings: CodeStyleSettings) {
        myCommenterForm.reset(settings)
    }

    override fun getPanel(): JComponent? {
        if (myPanel == null) {
            val contentPanel = getPanelInner()
            myPanel = JBScrollPane(contentPanel)
        }
        return myPanel!!
    }

    override fun createHighlighter(colors: EditorColorsScheme): EditorHighlighter {
        val fileType: FileType = getFileType()
        return FileTypeEditorHighlighterProviders.INSTANCE.forFileType(fileType)
            .getEditorHighlighter(null, fileType, null, colors)
    }

    companion object {
        fun getSupportedCommenterStandardOptionNames(): MutableList<String> {
            val SupportedCommenterStandardOptionNames = mutableListOf<String>().apply {
                val current = BuildNumber.currentVersion()

                add(CommenterOption.LINE_COMMENT_AT_FIRST_COLUMN.name)
                add(CommenterOption.LINE_COMMENT_ADD_SPACE.name)
                @Suppress("MissingRecentApi")
                if (current.compareTo(BuildNumber.fromString("221.5080.210")) >= 0)
                    add(CommenterOption.LINE_COMMENT_ADD_SPACE_ON_REFORMAT.name)
                add(CommenterOption.BLOCK_COMMENT_AT_FIRST_COLUMN.name)
                @Suppress("MissingRecentApi")
                if (current.compareTo(BuildNumber.fromString("213.5744.223")) >= 0)
                    add(CommenterOption.BLOCK_COMMENT_ADD_SPACE.name)
            }
            return SupportedCommenterStandardOptionNames
        }
    }
}