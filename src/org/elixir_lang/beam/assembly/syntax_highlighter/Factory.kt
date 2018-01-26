package org.elixir_lang.beam.assembly.syntax_highlighter

import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.beam.assembly.SyntaxHighlighter

class Factory : SyntaxHighlighterFactory() {
    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): com.intellij.openapi.fileTypes.SyntaxHighlighter {
        return SyntaxHighlighter()
    }
}
