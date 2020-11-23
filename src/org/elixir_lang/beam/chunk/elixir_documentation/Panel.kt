package org.elixir_lang.beam.chunk.elixir_documentation

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFileFactory
import com.intellij.util.containers.ContainerUtil.createWeakValueMap
import org.elixir_lang.ElixirFileType
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.beam.chunk.ElixirDocumentation
import java.awt.GridLayout
import java.lang.ref.WeakReference
import javax.swing.JPanel
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener

const val DEFAULT_TEXT = "# Select a module, definition, or clause to view its AST as code"

class Panel(private val elixirDocumentationTree: Tree, project: Project, private val moduleName: String?): JPanel(GridLayout(1, 1)), TreeSelectionListener {
    private val psiFile = PsiFileFactory.getInstance(project).createFileFromText(ElixirLanguage, DEFAULT_TEXT)
    private val document = PsiDocumentManager.getInstance(project).getDocument(psiFile)!!
    private val editor = EditorFactory.getInstance().createEditor(document, project, ElixirFileType.INSTANCE, true)

    private val callbackDocByCallbackDoc = createWeakValueMap<CallbackDoc, String>()
    private var callbackDocs = WeakReference<String>(null)
    private val docByDoc = createWeakValueMap<Doc, String>()
    private var docs = WeakReference<String>(null)
    private var elixirDocumentation = WeakReference<String>(null)
    private var moduleDoc = WeakReference<String>(null)
    private val typeDocByTypeDoc = createWeakValueMap<TypeDoc, String>()
    private var typeDocs = WeakReference<String>(null)

    init {
        elixirDocumentationTree.addTreeSelectionListener(this)
        add(editor.component)
    }

    override fun valueChanged(event: TreeSelectionEvent) {
        val lastPathComponent = event.path.lastPathComponent

        val text: String = when (lastPathComponent) {
            is CallbackDoc -> callbackDoc(lastPathComponent)
            is CallbackDocs -> callbackDocs(lastPathComponent)
            is Doc -> doc(lastPathComponent)
            is Docs -> docs(lastPathComponent)
            is ElixirDocumentation -> elixirDocumentation(lastPathComponent)
            is ModuleDoc -> moduleDoc(lastPathComponent)
            is TypeDoc -> typeDoc(lastPathComponent)
            is TypeDocs -> typeDocs(lastPathComponent)
            else -> DEFAULT_TEXT
        }

        ApplicationManager.getApplication().runWriteAction {
            document.setText(text)
        }
    }

    // Private Functions

    private fun arityToArgumentString(arity: Int) = 0.until(arity).joinToString(", ") { "p$it" }

    private fun callbackDoc(callbackDoc: CallbackDoc): String =
            callbackDocByCallbackDoc.computeIfAbsent(callbackDoc) { key ->
                val docAttributeDefinition = docAttributeDefinition("@doc", key.doc)
                val nameArity = key.nameArity
                val name = nameArity.name
                val attribute = key.kind.attribute
                val argumentsString = arityToArgumentString(nameArity.arity)

                docAttributeDefinition +
                        "$attribute $name($argumentsString) :: ..."
            }

    private fun callbackDocs(callbackDocs: CallbackDocs): String {
        val string = this.callbackDocs.get()

        return if (string != null) {
            string
        } else {
            val new = callbackDocs.callbackDocList.joinToString("\n\n") { callbackDoc(it) }

            this.callbackDocs = WeakReference(new)

            new
        }
    }

    private fun doc(doc: Doc): String =
            docByDoc.computeIfAbsent(doc) { key ->
                val docAttributeDefinition = docAttributeDefinition("@doc", key.doc)
                val macro = key.kind.macro
                val nameArity = key.nameArity
                val name = nameArity.name
                val argumentString = key.arguments.joinToString(", ")

                docAttributeDefinition +
                        "$macro $name($argumentString)"
            }

    private fun docs(docs: Docs): String {
        val cached = this.docs.get()

        return if (cached != null) {
            cached
        } else {
            val computed = docs.docList.joinToString("\n\n") { doc(it) }

            this.docs = WeakReference(computed)

            computed
        }
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

    private fun elixirDocumentation(elixirDocumentation: ElixirDocumentation): String {
        val cached = this.elixirDocumentation.get()

        return if (cached != null) {
            cached
        } else {
            val body = arrayOf(
                    elixirDocumentation.moduledoc?.let { moduleDoc(it).removeSuffix("\n") },
                    elixirDocumentation.typeDocs?.let { "# Types\n\n${typeDocs(it)}" },
                    elixirDocumentation.callbackDocs?.let { "# Callbacks\n\n${callbackDocs(it)}" },
                    elixirDocumentation.docs?.let { "# Macros and Functions\n\n${docs(it)}" }
            )
                    .filterNotNull()
                    .joinToString("\n\n")
                    .prependIndent("  ")

            val computed = "defmodule $moduleName do\n" +
                    "$body\n" +
                    "end"

            this.elixirDocumentation = WeakReference(computed)

            computed
        }
    }

    private fun moduleDoc(moduleDoc: ModuleDoc): String {
        val cached = this.moduleDoc.get()

        return if (cached != null) {
            cached
        } else {
            val computed = docAttributeDefinition("@moduledoc", moduleDoc.doc)

            this.moduleDoc = WeakReference(computed)

            computed
        }
    }

    private fun typeDoc(typeDoc: TypeDoc): String =
            typeDocByTypeDoc.computeIfAbsent(typeDoc) { key ->
                val docAttributeDefinition = docAttributeDefinition("@typedoc", key.doc)
                val attribute = key.kind.attribute
                val nameArity = key.nameArity
                val name = nameArity.name
                val arity = nameArity.arity
                val parenthesisString = if (arity > 0) {
                    "(${arityToArgumentString(arity)})"
                } else {
                    ""
                }

                docAttributeDefinition +
                        "$attribute $name$parenthesisString :: ..."
            }

    private fun typeDocs(typeDocs: TypeDocs): String {
        val cached = this.typeDocs.get()

        return if (cached != null) {
            cached
        } else {
            val computed = typeDocs.typeDocList.joinToString("\n\n") { typeDoc(it) }

            this.typeDocs = WeakReference(computed)

            computed
        }
    }
}
