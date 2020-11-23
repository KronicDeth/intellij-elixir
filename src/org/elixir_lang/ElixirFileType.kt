package org.elixir_lang

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

/**
 * Created by luke.imhoff on 7/27/14.
 */
open class ElixirFileType protected constructor() : LanguageFileType(ElixirLanguage) {
    override fun getName(): String = "Elixir"
    override fun getDescription(): String = "Elixir language file"
    override fun getDefaultExtension(): String = "ex"
    override fun getIcon(): Icon = Icons.FILE

    companion object {
        @JvmField
        val INSTANCE = ElixirFileType()
    }
}
