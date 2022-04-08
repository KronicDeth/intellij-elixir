package org.elixir_lang.psi.impl

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.util.Computable
import com.intellij.psi.PsiElement
import org.elixir_lang.Name
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.UNQUOTE
import org.elixir_lang.semantic.semantic
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
        when (val semantic = namedElement.semantic) {
            is org.elixir_lang.semantic.module_attribute.definition.Dynamic -> semantic.name
            is org.elixir_lang.semantic.implementation.Call -> semantic.canonicalName
            else -> {
                namedElement.nameIdentifier?.let { nameIdentifier ->
                    val text = ApplicationManager.getApplication().runReadAction(Computable { nameIdentifier.text })

                    unquoteName(namedElement, text)
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
    fun setName(variable: ElixirVariable, newName: String): PsiElement {
        TODO("Rename not implemented")
    }

    @JvmStatic
    fun setName(
        named: org.elixir_lang.psi.call.Named,
        newName: String
    ): PsiElement {
        val functionNameElement = named.functionNameElement()
        val newFunctionNameElementCall = ElementFactory.createUnqualifiedNoArgumentsCall(named.project, newName)

        val nameNode = functionNameElement!!.node
        val newNameNode = newFunctionNameElementCall.functionNameElement().node

        val node = nameNode.treeParent
        node.replaceChild(nameNode, newNameNode)

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
