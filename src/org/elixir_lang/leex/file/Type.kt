package org.elixir_lang.leex.file

import com.intellij.openapi.fileTypes.LanguageFileType

internal class Type : org.elixir_lang.eex.file.Type() {
    override fun getName(): String = "Live Embedded Elixir"
    override fun getDisplayName(): String = "LEEx"
    override fun getDescription(): String = "$name file"
    override fun getDefaultExtension(): String = "leex"

    companion object {
        val INSTANCE: LanguageFileType = Type()
    }
}
