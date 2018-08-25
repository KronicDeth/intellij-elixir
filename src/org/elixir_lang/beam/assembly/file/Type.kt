package org.elixir_lang.beam.assembly.file

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.beam.assembly.Icons
import org.elixir_lang.beam.assembly.Language
import javax.swing.Icon

object Type : LanguageFileType(Language) {
    override fun getCharset(file: VirtualFile, content: ByteArray): String = "UTF-8"
    override fun getDefaultExtension(): String = "beam.asm"
    override fun getDescription(): String = "Bogdan/Björn's Erlang Abstract Machine Code chunk assembly"
    override fun getIcon(): Icon = Icons.FILE
    override fun getName(): String = "BEAM Assembly file"
    override fun isReadOnly(): Boolean = true
}
