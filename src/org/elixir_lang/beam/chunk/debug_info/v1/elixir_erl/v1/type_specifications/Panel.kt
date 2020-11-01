@file:Suppress("DEPRECATION")

package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.type_specifications

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFileFactory
import com.intellij.util.containers.WeakValueHashMap
import org.elixir_lang.ElixirFileType
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.TypeSpecifications
import java.awt.GridLayout
import java.lang.ref.WeakReference
import javax.swing.JPanel
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener

const val DEFAULT_TEXT = "# Select a module, definition, or clause to view its AST as code"

class Panel(private val typeSpecificationTree: Tree, project: Project) : JPanel(GridLayout(1, 1)), TreeSelectionListener {
    private val psiFile = PsiFileFactory.getInstance(project).createFileFromText(ElixirLanguage, DEFAULT_TEXT)
    private val document = PsiDocumentManager.getInstance(project).getDocument(psiFile)!!
    private val editor = EditorFactory.getInstance().createEditor(document, project, ElixirFileType.INSTANCE, true)


    private var moduleString: WeakReference<String> = WeakReference<String>(null)

    private var typesModuleString: WeakReference<String> = WeakReference<String>(null)
    private var typesString: WeakReference<String> = WeakReference<String>(null)
    @Suppress("DEPRECATION")
    private val typeModuleStringByType = WeakValueHashMap<Type, String>()
    @Suppress("DEPRECATION")
    private val typeStringByType = WeakValueHashMap<Type, String>()

    private var opaquesModuleString: WeakReference<String> = WeakReference<String>(null)
    private var opaquesString: WeakReference<String> = WeakReference<String>(null)
    @Suppress("DEPRECATION")
    private val opaqueModuleStringByOpaque = WeakValueHashMap<Opaque, String>()
    @Suppress("DEPRECATION")
    private val opaqueStringByOpaque = WeakValueHashMap<Opaque, String>()

    private var callbacksModuleString: WeakReference<String> = WeakReference<String>(null)
    private var callbacksString: WeakReference<String> = WeakReference<String>(null)
    @Suppress("DEPRECATION")
    private val callbackModuleStringByCallback = WeakValueHashMap<Callback, String>()
    @Suppress("DEPRECATION")
    private val callbackStringByCallback = WeakValueHashMap<Callback, String>()

    private var optionalCallbacksModuleString: WeakReference<String> = WeakReference<String>(null)
    private var optionalCallbacksString: WeakReference<String> = WeakReference<String>(null)
    @Suppress("DEPRECATION")
    private val optionalCallbackModuleStringByOptionalCallback = WeakValueHashMap<OptionalCallback, String>()
    @Suppress("DEPRECATION")
    private val optionalCallbackStringByOptionalCallback = WeakValueHashMap<OptionalCallback, String>()

    private var specificationsModuleString: WeakReference<String> = WeakReference<String>(null)
    private var specificationsString: WeakReference<String> = WeakReference<String>(null)
    @Suppress("DEPRECATION")
    private val specificationModuleStringBySpecification = WeakValueHashMap<Specification, String>()
    @Suppress("DEPRECATION")
    private val specificationStringBySpecification = WeakValueHashMap<Specification, String>()

    init {
        typeSpecificationTree.addTreeSelectionListener(this)
        add(editor.component)
    }

    override fun valueChanged(event: TreeSelectionEvent) {
        val lastPathComponent = event.path.lastPathComponent

        val text: String = when (lastPathComponent) {
            is V1 -> moduleString(lastPathComponent)
            is Types -> typesModuleString(lastPathComponent, event.path.parentPath.lastPathComponent as V1)
            is Type -> typeModuleString(lastPathComponent, event.path.parentPath.parentPath.lastPathComponent as V1)
            is Opaques -> opaquesModuleString(lastPathComponent, event.path.parentPath.lastPathComponent as V1)
            is Opaque -> opaqueModuleString(lastPathComponent, event.path.parentPath.parentPath.lastPathComponent as V1)
            is Callbacks -> callbacksModuleString(lastPathComponent, event.path.parentPath.lastPathComponent as V1)
            is Callback -> callbackModuleString(lastPathComponent, event.path.parentPath.parentPath.lastPathComponent as V1)
            is OptionalCallbacks -> optionalCallbacksModuleString(lastPathComponent, event.path.parentPath.lastPathComponent as V1)
            is OptionalCallback -> optionalCallbackModuleString(lastPathComponent, event.path.parentPath.parentPath.lastPathComponent as V1)
            is Specifications -> specificationsModuleString(lastPathComponent, event.path.parentPath.lastPathComponent as V1)
            is Specification -> specificationModuleString(lastPathComponent, event.path.parentPath.parentPath.lastPathComponent as V1)
            else -> DEFAULT_TEXT
        }

        ApplicationManager.getApplication().runWriteAction {
            document.setText(text)
        }
    }

    private fun moduleString(debugInfo: V1): String {
        val moduleString = this.moduleString.get()

        return if (moduleString != null) {
            moduleString
        } else {
            val newModuleString = debugInfo.moduleContext {
                val typeSpecifications = debugInfo.typeSpecifications!!

                arrayOf(
                        typesString(typeSpecifications.types),
                        opaquesString(typeSpecifications.opaques),
                        callbacksString(typeSpecifications.callbacks),
                        optionalCallbacksString(typeSpecifications.optionalCallbacks),
                        specificationsString(typeSpecifications.specifications)
                )
                        .filter { it.isNotBlank() }
                        .joinToString("\n\n")
            }

            this.moduleString = WeakReference(newModuleString)

            newModuleString
        }
    }

    private fun typesModuleString(types: Types, debugInfo: V1): String {
        val typesModuleString = this.typesModuleString.get()

        return if (typesModuleString != null) {
            typesModuleString
        } else {
            val newTypesModuleString = debugInfo.moduleContext {
                typesString(types)
            }

            this.typesModuleString = WeakReference(newTypesModuleString)

            newTypesModuleString
        }
    }

    private fun typesString(types: Types): String {
        val typesString = this.typesString.get()

        return if (typesString != null) {
            typesString
        } else {
            val typeSpecificationTreeModel = typeSpecificationTree.model
            val typeSpecifications = typeSpecificationTreeModel.root.let { it as V1 }.typeSpecifications!!
            val typeCount = typeSpecificationTreeModel.getChildCount(types)

            val newTypesString = (0 until typeCount).joinToString("\n\n") { index ->
                val type = typeSpecificationTreeModel.getChild(types, index) as Type

                typeString(type, typeSpecifications)
            }

            this.typesString = WeakReference(newTypesString)

            newTypesString
        }
    }

    private fun typeModuleString(type: Type, debugInfo: V1): String =
            typeModuleStringByType.computeIfAbsent(type) { key ->
                debugInfo.moduleContext {
                    typeString(key, debugInfo.typeSpecifications!!)
                }
            }

    private fun typeString(type: Type, typeSpecifications: TypeSpecifications): String =
            typeStringByType.computeIfAbsent(type) { key ->
                key.toString(typeSpecifications)
            }

    private fun opaquesModuleString(opaques: Opaques, debugInfo: V1): String {
        val opaquesModuleString = this.opaquesModuleString.get()

        return if (opaquesModuleString != null) {
            opaquesModuleString
        } else {
            val newOpaquesModuleString = debugInfo.moduleContext {
                opaquesString(opaques)
            }

            this.opaquesModuleString = WeakReference(newOpaquesModuleString)

            newOpaquesModuleString
        }
    }

    private fun opaquesString(opaques: Opaques): String {
        val opaquesString = this.opaquesString.get()

        return if (opaquesString != null) {
            opaquesString
        } else {
            val typeSpecificationTreeModel = typeSpecificationTree.model
            val opaqueCount = typeSpecificationTreeModel.getChildCount(opaques)

            val newOpaquesString = (0 until opaqueCount).joinToString("\n\n") { index ->
                val opaque = typeSpecificationTreeModel.getChild(opaques, index) as Opaque

                opaqueString(opaque)
            }

            this.opaquesString = WeakReference(newOpaquesString)

            newOpaquesString
        }
    }

    private fun opaqueModuleString(opaque: Opaque, debugInfo: V1): String =
            opaqueModuleStringByOpaque.computeIfAbsent(opaque) { key ->
                debugInfo.moduleContext {
                    opaqueString(key)
                }
            }

    private fun opaqueString(opaque: Opaque): String =
            opaqueStringByOpaque.computeIfAbsent(opaque) { key ->
                key.toString()
            }

    private fun callbacksModuleString(callbacks: Callbacks, debugInfo: V1): String {
        val callbacksModuleString = this.callbacksModuleString.get()

        return if (callbacksModuleString != null) {
            callbacksModuleString
        } else {
            val newCallbacksModuleString = debugInfo.moduleContext {
                callbacksString(callbacks)
            }

            this.callbacksModuleString = WeakReference(newCallbacksModuleString)

            newCallbacksModuleString
        }
    }

    private fun callbacksString(callbacks: Callbacks): String {
        val callbacksString = this.callbacksString.get()

        return if (callbacksString != null) {
            callbacksString
        } else {
            val typeSpecificationTreeModel = typeSpecificationTree.model
            val callbackCount = typeSpecificationTreeModel.getChildCount(callbacks)

            val newCallbacksString = (0 until callbackCount).joinToString("\n\n") { index ->
                val callback = typeSpecificationTreeModel.getChild(callbacks, index) as Callback

                callbackString(callback)
            }

            this.callbacksString = WeakReference(newCallbacksString)

            newCallbacksString
        }
    }

    private fun callbackModuleString(callback: Callback, debugInfo: V1): String =
            callbackModuleStringByCallback.computeIfAbsent(callback) { key ->
                debugInfo.moduleContext {
                    callbackString(key)
                }
            }

    private fun callbackString(callback: Callback): String =
            callbackStringByCallback.computeIfAbsent(callback, Callback::toString)

    private fun optionalCallbacksModuleString(optionalCallbacks: OptionalCallbacks, debugInfo: V1): String {
        val optionalCallbacksModuleString = this.optionalCallbacksModuleString.get()

        return if (optionalCallbacksModuleString != null) {
            optionalCallbacksModuleString
        } else {
            val newOptionalCallbacksModuleString = debugInfo.moduleContext {
                optionalCallbacksString(optionalCallbacks)
            }

            this.optionalCallbacksModuleString = WeakReference(newOptionalCallbacksModuleString)

            newOptionalCallbacksModuleString
        }
    }

    private fun optionalCallbacksString(optionalCallbacks: OptionalCallbacks): String {
        val optionalCallbacksString = this.optionalCallbacksString.get()

        return if (optionalCallbacksString != null) {
            optionalCallbacksString
        } else {
            val typeSpecificationTreeModel = typeSpecificationTree.model
            val optionalCallbackCount = typeSpecificationTreeModel.getChildCount(optionalCallbacks)

            val newOptionalCallbacksString =
                    if (optionalCallbackCount > 0) {
                        (0 until optionalCallbackCount)
                                .joinToString(",\n                    ") { index ->
                                    val optionalCallback = typeSpecificationTreeModel.getChild(optionalCallbacks, index) as OptionalCallback

                                    optionalCallbackString(optionalCallback)
                                }.let {
                                    "@optional_callbacks $it"
                                }
                    } else {
                        ""
                    }

            this.optionalCallbacksString = WeakReference(newOptionalCallbacksString)

            newOptionalCallbacksString
        }
    }

    private fun optionalCallbackModuleString(optionalCallback: OptionalCallback, debugInfo: V1): String =
            optionalCallbackModuleStringByOptionalCallback.computeIfAbsent(optionalCallback) { key ->
                debugInfo.moduleContext {
                    "@optional_callbacks ${optionalCallbackString(key)}"
                }
            }

    private fun optionalCallbackString(optionalCallback: OptionalCallback): String =
            optionalCallbackStringByOptionalCallback.computeIfAbsent(optionalCallback) {
                "${it.name}: ${it.arity}"
            }

    private fun specificationsModuleString(specifications: Specifications, debugInfo: V1): String {
        val specificationsModuleString = this.specificationsModuleString.get()

        return if (specificationsModuleString != null) {
            specificationsModuleString
        } else {
            val newSpecificationsModuleString = debugInfo.moduleContext {
                specificationsString(specifications)
            }

            this.specificationsModuleString = WeakReference(newSpecificationsModuleString)

            newSpecificationsModuleString
        }
    }

    private fun specificationsString(specifications: Specifications): String {
        val specificationsString = this.specificationsString.get()

        return if (specificationsString != null) {
            specificationsString
        } else {
            val typeSpecificationTreeModel = typeSpecificationTree.model
            val specificationCount = typeSpecificationTreeModel.getChildCount(specifications)

            val newSpecificationsString = (0 until specificationCount).joinToString("\n\n") { index ->
                val specification = typeSpecificationTreeModel.getChild(specifications, index) as Specification

                specificationString(specification)
            }

            this.specificationsString = WeakReference(newSpecificationsString)

            newSpecificationsString
        }
    }

    private fun specificationModuleString(specification: Specification, debugInfo: V1): String =
            specificationModuleStringBySpecification.computeIfAbsent(specification) { key ->
                debugInfo.moduleContext {
                    specificationString(key)
                }
            }

    private fun specificationString(specification: Specification): String =
            specificationStringBySpecification.computeIfAbsent(specification) { key ->
                key.toString()
            }
}
