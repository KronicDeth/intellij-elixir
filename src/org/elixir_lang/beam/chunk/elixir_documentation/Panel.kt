package org.elixir_lang.beam.chunk.elixir_documentation

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFileFactory
import org.elixir_lang.ElixirFileType
import org.elixir_lang.ElixirLanguage
import java.awt.GridLayout
import javax.swing.JPanel
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener

const val DEFAULT_TEXT = "# Select a module, definition, or clause to view its AST as code"

class Panel(private val elixirDocumentationTree: Tree, project: Project): JPanel(GridLayout(1, 1)), TreeSelectionListener {
    private val psiFile = PsiFileFactory.getInstance(project).createFileFromText(ElixirLanguage.INSTANCE, DEFAULT_TEXT)
    private val document = PsiDocumentManager.getInstance(project).getDocument(psiFile)!!
    private val editor = EditorFactory.getInstance().createEditor(document, project, ElixirFileType.INSTANCE, true)

    init {
        elixirDocumentationTree.addTreeSelectionListener(this)
        add(editor.component)
    }

    override fun valueChanged(event: TreeSelectionEvent) {
        val lastPathComponent = event.path.lastPathComponent

        val text: String = when (lastPathComponent) {
            is CallbackDoc -> callbackDoc(lastPathComponent)
            is Doc -> doc(lastPathComponent)
            is ModuleDoc -> moduleDoc(lastPathComponent)
            is TypeDoc -> typeDoc(lastPathComponent)
            else -> DEFAULT_TEXT
        }

        ApplicationManager.getApplication().runWriteAction {
            document.setText(text)
        }
    }

    // Private Functions

    private fun arityToArgumentString(arity: Int) = 0.until(arity).joinToString(", ") { "p$it" }

    private fun callbackDoc(callbackDoc: CallbackDoc): String {
        val docAttributeDefinition = docAttributeDefinition("@doc", callbackDoc.doc)
        val nameArity = callbackDoc.nameArity
        val name = nameArity.name
        val attribute = callbackDoc.kind.attribute
        val argumentsString = arityToArgumentString(nameArity.arity)

        return docAttributeDefinition +
                "$attribute $name($argumentsString) :: ..."
    }

    private fun doc(doc: Doc): String {
        val docAttributeDefinition = docAttributeDefinition("@doc", doc.doc)
        val macro = doc.kind.macro
        val nameArity = doc.nameArity
        val name = nameArity.name
        val argumentString = doc.arguments.joinToString(", ")

        return docAttributeDefinition +
                "$macro $name($argumentString)"
    }

    private fun docAttributeDefinition(attribute: String, doc: Any?): String =
            when (doc) {
                is String ->
                    "$attribute \"\"\"\n" +
                            doc +
                            "\"\"\"\n"
                false ->
                    "$attribute false\n"
                else ->
                    ""
            }

    private fun moduleDoc(moduleDoc: ModuleDoc): String =
            docAttributeDefinition("@moduledoc", moduleDoc.doc)

    private fun typeDoc(typeDoc: TypeDoc): String {
        val docAttributeDefinition = docAttributeDefinition("@typedoc", typeDoc.doc)
        val attribute = typeDoc.kind.attribute
        val nameArity = typeDoc.nameArity
        val name = nameArity.name
        val arity = nameArity.arity
        val parenthesisString = if (arity > 0) {
            "(${arityToArgumentString(arity)})"
        } else {
            ""
        }

        return docAttributeDefinition +
                "$attribute $name$parenthesisString :: ..."
    }
}
