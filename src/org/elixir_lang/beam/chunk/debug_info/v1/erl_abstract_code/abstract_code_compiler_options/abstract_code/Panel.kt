@file:Suppress("DEPRECATION")

package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFileFactory
import com.intellij.util.containers.ContainerUtil.createWeakValueMap
import org.elixir_lang.ElixirFileType
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.Macro.adjustNewLines
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.AbstractCodeCompileOptions
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.attribute.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.function.Clause
import java.awt.GridLayout
import java.lang.ref.WeakReference
import javax.swing.JPanel
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener

const val DEFAULT_TEXT = "# Select Form"

class Panel(private val formsTree: Tree, project: Project): JPanel(GridLayout(1, 1)), TreeSelectionListener {
    private val psiFile = PsiFileFactory.getInstance(project).createFileFromText(ElixirLanguage, DEFAULT_TEXT)
    private val document = PsiDocumentManager.getInstance(project).getDocument(psiFile)!!
    private val editor = EditorFactory.getInstance().createEditor(
            document,
            project,
            ElixirFileType.INSTANCE,
            true
    )

    private val attributeByToMacroString = createWeakValueMap<ToMacroString, String>()

    private val attributeModuleByAttribute = createWeakValueMap<ToMacroString, String>()

    private var attributes: WeakReference<String> = WeakReference<String>(null)
    private var attributesModule: WeakReference<String> = WeakReference<String>(null)

    private var clauseByClause = createWeakValueMap<Clause, String>()
    private var clauseModuleByClause = createWeakValueMap<Clause, String>()

    private val functionByFunction = createWeakValueMap<Function, String>()

    private val functionModuleByFunction = createWeakValueMap<Function, String>()

    private var functions: WeakReference<String> = WeakReference<String>(null)
    private var functionsModule: WeakReference<String> = WeakReference<String>(null)

    private var module: WeakReference<String> = WeakReference<String>(null)

    init {
        formsTree.addTreeSelectionListener(this)
        add(editor.component)
    }

    private fun attribute(attribute: ToMacroString): String =
        attributeByToMacroString.computeIfAbsent(attribute) { key ->
            key.toMacroString()
        }

    private fun attributeModule(debugInfo: AbstractCodeCompileOptions, attribute: ToMacroString): String =
        attributeModuleByAttribute.computeIfAbsent(attribute) { key ->
            moduleContext(debugInfo) {
                attribute(key)
            }
        }

    private fun attributes(attributes: Attributes): String {
        val formsTreeModel = formsTree.model
        val attributeCount = formsTreeModel.getChildCount(attributes)

        return (0 until attributeCount).joinToString("\n\n") { index ->
            val attribute = formsTreeModel.getChild(attributes, index) as ToMacroString

            attribute(attribute)
        }
    }

    private fun attributesModule(debugInfo: AbstractCodeCompileOptions, attributes: Attributes): String {
        val attributesModule = this.attributesModule.get()

        return if (attributesModule != null) {
            attributesModule
        } else {
            val newAttributesModule = moduleContext(debugInfo) {
                attributes(attributes)
            }

            this.attributesModule = WeakReference(newAttributesModule)

            newAttributesModule
        }
    }

    private fun clause(clause: Clause): String =
            clauseByClause.computeIfAbsent(clause) { key ->
                key.toMacroString()
            }

    private fun clauseModule(debugInfo: AbstractCodeCompileOptions, clause: Clause): String =
        clauseModuleByClause.computeIfAbsent(clause) { key ->
            moduleContext(debugInfo) {
                clause(key)
            }
        }

    private fun function(function: Function): String =
            functionByFunction.computeIfAbsent(function) { key ->
                key.toMacroString()
            }

    private fun functionModule(debugInfo: AbstractCodeCompileOptions, function: Function): String =
            functionModuleByFunction.computeIfAbsent(function) { key ->
                moduleContext(debugInfo) {
                    function(key)
                }
            }

    private fun functions(functions: Functions): String {
        val formsTreeModel = formsTree.model
        val functionCount = formsTreeModel.getChildCount(functions)

        return (0 until functionCount).joinToString("\n\n") { index ->
            val function = formsTreeModel.getChild(functions, index) as Function

            function(function)
        }
    }

    private fun functionsModule(debugInfo: AbstractCodeCompileOptions, functions: Functions): String {
        val functionsModule = this.functionsModule.get()

        return if (functionsModule != null) {
            functionsModule
        } else {
            val newFunctionsModule = moduleContext(debugInfo) {
                functions(functions)
            }

            this.functionsModule = WeakReference(newFunctionsModule)

            newFunctionsModule
        }
    }

    private fun module(debugInfo: AbstractCodeCompileOptions): String {
        val module = this.module.get()

        return if (module != null) {
            module
        } else {
            val newModule = moduleContext(debugInfo) {
                StringBuilder()
                        .append(attributes(debugInfo.attributes))
                        .append("\n\n")
                        .append(functions(debugInfo.functions))
                        .toString()
            }

            this.module = WeakReference(newModule)

            newModule
        }
    }

    private fun moduleContext(debugInfo: AbstractCodeCompileOptions, inner: () -> String): String =
            "defmodule ${debugInfo.inspectedModule ?: "..."} do \n" +
                    "  # ... \n" +
                    "  ${adjustNewLines(inner(), "\n  ")}\n" +
                    "  # ...\n" +
                    "end"

    override fun valueChanged(event: TreeSelectionEvent) {
        val path = event.path
        val lastPathComponent = path.lastPathComponent

        val text: String = when (lastPathComponent) {
            is AbstractCodeCompileOptions -> module(lastPathComponent)

            is Attributes -> attributesModule(
                    path.parentPath.lastPathComponent as AbstractCodeCompileOptions,
                    lastPathComponent
            )
            is Clause -> clauseModule(
                    path.parentPath.parentPath.parentPath.lastPathComponent as AbstractCodeCompileOptions,
                    lastPathComponent
            )
            is Function -> functionModule(
                    path.parentPath.parentPath.lastPathComponent as AbstractCodeCompileOptions,
                    lastPathComponent
            )
            is Functions -> functionsModule(
                    path.parentPath.lastPathComponent as AbstractCodeCompileOptions,
                    lastPathComponent
            )
            is MacroString -> attributeModule(
                    path.parentPath.parentPath.lastPathComponent as AbstractCodeCompileOptions,
                    lastPathComponent
            )
            else -> DEFAULT_TEXT
        }

        ApplicationManager.getApplication().runWriteAction {
            document.setText(text)
        }
    }
}
