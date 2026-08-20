package org.elixir_lang.heex.file

import com.intellij.lang.Language
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.fileTypes.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.heex.HeexLanguage
import org.elixir_lang.heex.Icons
import org.elixir_lang.heex.TemplateHighlighter
import javax.swing.Icon

// See https://github.com/JetBrains/intellij-plugins/blob/500f42337a87f463e0340f43e2411266fcfa9c5f/handlebars/src/com/dmarcotte/handlebars/file/HbFileType.java
internal open class Type protected constructor(lang: Language? = HeexLanguage.INSTANCE) :
    LanguageFileType(lang!!), TemplateLanguageFileType {
    override fun getName(): String = "HTML Embedded Elixir"
    override fun getDescription(): String = "HTML Embedded Elixir file"
    override fun getDefaultExtension(): String = DEFAULT_EXTENSION
    override fun getIcon(): Icon? = Icons.FILE

    companion object {
        private const val DEFAULT_EXTENSION = "heex"

        @JvmField
        val INSTANCE: LanguageFileType = Type()
    }

    init {
        FileTypeEditorHighlighterProviders.getInstance().addExplicitExtension(
            this
        ) { project: Project?, _: FileType?, virtualFile: VirtualFile?, editorColorsScheme: EditorColorsScheme? ->
            TemplateHighlighter(
                project,
                virtualFile,
                editorColorsScheme!!
            )
        }
    }
}
