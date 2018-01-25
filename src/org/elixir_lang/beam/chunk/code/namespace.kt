package org.elixir_lang.beam.chunk.code

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFileFactory
import org.elixir_lang.beam.assembly.Language
import org.elixir_lang.beam.assembly.file.Type
import org.elixir_lang.beam.chunk.Code
import javax.swing.JComponent

private const val DEFAULT_TEXT = "# Could not disassemble Code Chunk"

fun component(code: Code?, project: Project): JComponent {
    val text = code?.assembly() ?: DEFAULT_TEXT
    val psiFile = PsiFileFactory.getInstance(project).createFileFromText(Language, text)
    val document = PsiDocumentManager.getInstance(project).getDocument(psiFile)!!
    val editor = EditorFactory.getInstance().createEditor(document, project, Type, true)

    return editor.component
}
