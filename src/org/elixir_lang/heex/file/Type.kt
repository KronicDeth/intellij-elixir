package org.elixir_lang.heex.file

import com.intellij.lang.html.HTMLLanguage
import com.intellij.openapi.fileTypes.LanguageFileType
import org.elixir_lang.heex.Icons
import javax.swing.Icon

class Type : org.elixir_lang.eex.file.Type() {
    override fun getName(): String = "HEEx"
    override fun getDisplayName(): String = "HEEx"
    override fun getDescription(): String = "HTML+EEx template file"
    override fun getDefaultExtension(): String = "heex"
    override fun getIcon(): Icon = Icons.FILE
    override fun defaultTemplateDataLanguage(): com.intellij.lang.Language = HTMLLanguage.INSTANCE

    companion object {
        val INSTANCE: LanguageFileType = Type()
    }
}
