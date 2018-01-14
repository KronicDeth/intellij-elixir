package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.definition.clause

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFileFactory
import org.elixir_lang.ElixirFileType
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.Macro
import org.elixir_lang.Macro.adjustNewLines
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.Tree
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.definition.Clause
import java.awt.GridLayout
import javax.swing.JPanel
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener

const val DEFAULT_TEXT = "# Select a clause to view its AST as code"

class Panel(definitionsTree: Tree, project: Project): JPanel(GridLayout(1, 1)), TreeSelectionListener {
    private val psiFile = PsiFileFactory.getInstance(project).createFileFromText(ElixirLanguage.INSTANCE, DEFAULT_TEXT)
    private val document = PsiDocumentManager.getInstance(project).getDocument(psiFile)!!
    private val editor = EditorFactory.getInstance().createEditor(document, project, ElixirFileType.INSTANCE, true)

    init {
        definitionsTree.addTreeSelectionListener(this)
        add(editor.component)
    }

    override fun valueChanged(event: TreeSelectionEvent) {
        val text = (event.path.lastPathComponent as? Clause)?.let { clause ->
            blockToStringWithContext(clause)
        } ?: DEFAULT_TEXT

        ApplicationManager.getApplication().runWriteAction {
            document.setText(text)
        }
    }

    private fun blockToStringWithContext(clause: Clause) =
            moduleContext(clause) {
                headContext(clause) {
                    Macro.toString(clause.block)
                }
            }

    private fun headContext(clause: Clause, body: () -> String): String =
            "def ${clause.head} do\n  ${adjustNewLines(body(), "\n  ")}\nend"

    private fun moduleContext(clause: Clause, inner: () -> String): String =
            "defmodule ${clause.definition.debugInfo.inspectedModule!!} do\n" +
                    "  # ...\n" +
                    "  ${adjustNewLines(inner(), "\n  ")}\n" +
                    "  #...\n" +
                    "end"
}
