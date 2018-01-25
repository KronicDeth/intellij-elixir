package org.elixir_lang.beam.assembly

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import org.elixir_lang.icons.ElixirIcons
import javax.swing.Icon

class File(fileViewProvider: FileViewProvider): PsiFileBase(fileViewProvider, Language) {
    override fun getFileType(): FileType = org.elixir_lang.beam.assembly.file.Type
    override fun getIcon(flags: Int): Icon = ElixirIcons.FILE
}
