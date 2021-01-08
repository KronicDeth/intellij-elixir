@file:Suppress("DEPRECATION")

package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.definition.clause

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFileFactory
import com.intellij.util.containers.ContainerUtil.createWeakValueMap
import org.elixir_lang.ElixirFileType
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.Macro
import org.elixir_lang.Macro.adjustNewLines
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.Definition
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.Tree
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.definition.Clause
import java.awt.GridLayout
import java.lang.ref.WeakReference
import javax.swing.JPanel
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener

const val DEFAULT_TEXT = "# Select a module, definition, or clause to view its AST as code"

class Panel(private val definitionsTree: Tree, project: Project): JPanel(GridLayout(1, 1)), TreeSelectionListener {
    private val psiFile = PsiFileFactory.getInstance(project).createFileFromText(ElixirLanguage, DEFAULT_TEXT)
    private val document = PsiDocumentManager.getInstance(project).getDocument(psiFile)!!
    private val editor = EditorFactory.getInstance().createEditor(document, project, ElixirFileType.INSTANCE, true)

    private val clauseHeadByClause = createWeakValueMap<Clause, String>()
    private val clauseHeadModuleByClause = createWeakValueMap<Clause, String>()
    private val definitionByDefinition = createWeakValueMap<Definition, String>()
    private val definitionModuleByClause = createWeakValueMap<Definition, String>()
    private var module: WeakReference<String> = WeakReference<String>(null)

    init {
        definitionsTree.addTreeSelectionListener(this)
        add(editor.component)
    }

    private fun clauseHead(clause: Clause): String =
            clauseHeadByClause.computeIfAbsent(clause) { key ->
                headContext(key) {
                    Macro.toString(key.block)
                }
            }

    private fun clauseHeadModule(clause: Clause): String =
            clauseHeadModuleByClause.computeIfAbsent(clause) { key ->
                moduleContext(key) {
                    clauseHead(key)
                }
            }

    private fun definition(definition: Definition): String =
            definitionByDefinition.computeIfAbsent(definition) { key ->
                val definitionsTreeModel = definitionsTree.model
                val clauseCount = definitionsTreeModel.getChildCount(key)

                (0 until clauseCount).joinToString("\n\n") { index ->
                    val clause = definitionsTreeModel.getChild(key, index) as Clause

                    clauseHead(clause)
                }
            }

    private fun definitionModule(definition: Definition): String =
            definitionModuleByClause.computeIfAbsent(definition) { key ->
                moduleContext(key) {
                    definition(key)
                }
            }

    private fun module(debugInfo: V1): String {
        val module = this.module.get()

        return if (module != null) {
            module
        } else {
            val newModule = debugInfo.moduleContext {
                val definitionsTreeModel = definitionsTree.model
                val definitionCount = definitionsTreeModel.getChildCount(debugInfo)

                (0 until definitionCount).joinToString("\n\n") { index ->
                    val definition = definitionsTreeModel.getChild(debugInfo, index) as Definition

                    definition(definition)
                }
            }

            this.module = WeakReference(newModule)

            newModule
        }
    }

    override fun valueChanged(event: TreeSelectionEvent) {
        val lastPathComponent = event.path.lastPathComponent

        val text: String = when (lastPathComponent) {
            is Clause -> clauseHeadModule(lastPathComponent)
            is Definition -> definitionModule(lastPathComponent)
            is V1 -> module(lastPathComponent)
            else -> DEFAULT_TEXT
        }

        ApplicationManager.getApplication().runWriteAction {
            document.setText(text)
        }
    }

    private fun headContext(clause: Clause, body: () -> String): String =
            "def ${clause.head} do\n  ${adjustNewLines(body(), "\n  ")}\nend"

    private fun moduleContext(clause: Clause, inner: () -> String): String = moduleContext(clause.definition, inner)

    private fun moduleContext(definition: Definition, inner: () -> String): String =
            definition.debugInfo.moduleContext(inner)
}
