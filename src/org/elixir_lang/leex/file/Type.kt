package org.elixir_lang.leex.file

import com.intellij.openapi.fileTypes.LanguageFileType

class Type : org.elixir_lang.eex.file.Type() {
    override fun getName(): String = "Live Embedded Elixir"
    override fun getDescription(): String = "Live Embedded Elixir file"
    override fun getDefaultExtension(): String = "leex"

    companion object {
        val INSTANCE: LanguageFileType = Type()
    }
}
