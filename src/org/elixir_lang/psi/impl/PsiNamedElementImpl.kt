package org.elixir_lang.psi.impl

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.util.Computable
import com.intellij.psi.PsiElement
import org.elixir_lang.Name
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.module.PutAttribute
import org.elixir_lang.module.RegisterAttribute
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.UNQUOTE
import org.jetbrains.annotations.Contract

object PsiNamedElementImpl {
    @JvmStatic
    fun getName(alias: ElixirAlias): String {
        return alias.text
    }

    @JvmStatic
    fun getName(qualifiedAlias: QualifiedAlias): String = runReadAction { qualifiedAlias.text }

    @JvmStatic
    fun getName(atom: ElixirAtom): String? =
        if (atom.line == null) {
            atom.node.lastChildNode.text
        } else {
            null
        }

    @Contract(pure = true)
    @JvmStatic
    fun getName(namedElement: NamedElement): String? =
        if (namedElement is Call && RegisterAttribute.`is`(namedElement)) {
            RegisterAttribute.name(namedElement)
        } else if (namedElement is Call && PutAttribute.`is`(namedElement)) {
            PutAttribute.name(namedElement)
        } else {
            val nameIdentifier = namedElement.nameIdentifier

            if (nameIdentifier != null) {
                val text = ApplicationManager.getApplication().runReadAction(Computable { nameIdentifier.text })
                unquoteName(namedElement, text)
            } else {
                if (namedElement is Call) {
                    val call = namedElement as Call

                    /* The name of the module defined by {@code defimpl PROTOCOL[ for: MODULE]} is derived by combining the
                   PROTOCOL and MODULE name into PROTOCOL.MODULE.  Neither piece is really the "name" or
                   "nameIdentifier" element of the implementation because changing the PROTOCOL make the implementation
                   just for that different Protocol and changing the MODULE makes the implementation for a different
                   MODULE.  If `for:` isn't given, it's really the enclosing {@code defmodule MODULE} whose name should
                   be changed. */
                    if (Implementation.`is`(call)) {
                        Implementation.name(call)
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
        }

    @JvmStatic
    fun setName(
        @Suppress("UNUSED_PARAMETER") element: PsiElement,
        @Suppress("UNUSED_PARAMETER") newName: String
    ): PsiElement {
        TODO("Rename not implemented")
    }

    @JvmStatic
    fun setName(
        atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>,
        newName: String
    ): PsiElement {
        val newAtUnqualifiedNoParenthesesCall = ElementFactory.createModuleAttributeDeclaration(
            atUnqualifiedNoParenthesesCall.project,
            newName,
            "dummy_value"
        )

        val nameNode = atUnqualifiedNoParenthesesCall.atIdentifier.node
        val newNameNode = newAtUnqualifiedNoParenthesesCall.atIdentifier.node

        val node = atUnqualifiedNoParenthesesCall.node
        node.replaceChild(nameNode, newNameNode)

        return atUnqualifiedNoParenthesesCall
    }

    @JvmStatic
    fun setName(
        @Suppress("UNUSED_PARAMETER") variable: ElixirVariable,
        @Suppress("UNUSED_PARAMETER") newName: String
    ): PsiElement {
        TODO("Rename not implemented")
    }

    @JvmStatic
    fun setName(
        named: org.elixir_lang.psi.call.Named,
        newName: String
    ): PsiElement {
        if (CallDefinitionClause.`is`(named)) {
            CallDefinitionClause.head(named)?.let { it as? org.elixir_lang.psi.call.Named }?.setName(newName)
        } else {
            val functionNameElement = named.functionNameElement()
            val nameNode = functionNameElement!!.node
            val nameElementType = nameNode.elementType

            val newNameNode = when (functionNameElement) {
                is ElixirIdentifier -> {
                    val newFunctionNameElementCall =
                        ElementFactory.createUnqualifiedNoArgumentsCall(named.project, newName)

                    newFunctionNameElementCall.functionNameElement().node
                }

                is ElixirRelativeIdentifier -> {
                    val newFunctionNameElementCall =
                        ElementFactory.createQualifiedNoArgumentsCall(named.project, "Qualifier", newName)

                    newFunctionNameElementCall.functionNameElement()!!.node
                }

                else -> {
                    Logger.error(javaClass, "Don't know how to replace function name", named)

                    nameNode
                }
            }

            val newNameElementType = newNameNode.elementType

            if (nameElementType == newNameElementType) {
                val node = nameNode.treeParent
                node.replaceChild(nameNode, newNameNode)
            } else {
                Logger.error(
                    javaClass,
                    "New name node elementType (${newNameElementType}) does not match old name node elementType (${nameElementType})",
                    named
                )
            }
        }

        return named
    }

    /**
     * If `name` is `"unquote"` then the [Call.primaryArguments] single argument is added to the
     * name.
     */
    @JvmStatic
    fun unquoteName(named: PsiElement, name: Name): Name = if (named is Call && UNQUOTE == name) {
        val primaryArguments = named.primaryArguments()

        if (primaryArguments != null && primaryArguments.size == 1) {
            val primaryArgument = primaryArguments[0]!!

            primaryArgument
                .let { it as? ElixirAccessExpression }
                ?.children
                ?.singleOrNull()
                ?.let { it as? ElixirAtom }
                ?.let { atom ->
                    val body = atom.line?.body

                    if (body != null) {
                        if (body.children.isEmpty()) {
                            body.text
                        } else {
                            null
                        }
                    } else {
                        atom.node.lastChildNode.text
                    }
                } ?: "${name}(${primaryArgument.text})"
        } else {
            null
        }
    } else {
        null
    } ?: name
}
