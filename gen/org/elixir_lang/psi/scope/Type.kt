package org.elixir_lang.psi.scope

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.isAncestor
import com.intellij.psi.util.siblings
import com.intellij.util.xml.Resolve
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.call.macroChildCalls
import org.elixir_lang.psi.impl.identifierName
import org.elixir_lang.psi.stub.type.UnmatchedUnqualifiedNoArgumentsCall
import org.elixir_lang.reference.ModuleAttribute.Companion.isSpecificationName
import org.elixir_lang.reference.ModuleAttribute.Companion.isTypeName
import org.elixir_lang.structure_view.element.modular.Module

abstract class Type : PsiScopeProcessor {
    override fun execute(element: PsiElement, state: ResolveState): Boolean =
            when (element) {
                is Call -> execute(element, state)
                // Anonymous function type siganture
                is ElixirStabNoParenthesesSignature -> execute(element, state)
                is ElixirNoParenthesesOneArgument, is ElixirAccessExpression -> executeOnChildren(element, state)
                is ElixirFile, is ElixirList -> false
                else -> {
                    TODO("Not yet implemented")
                }
            }


    protected abstract fun executeOnType(definition: AtUnqualifiedNoParenthesesCall<*>, state: ResolveState): Boolean
    protected abstract fun executeOnParameter(parameter: UnqualifiedNoArgumentsCall<*>, state: ResolveState): Boolean
    protected abstract fun keepProcessing(): Boolean

    private fun execute(call: Call, state: ResolveState): Boolean =
            when (call) {
                is UnqualifiedNoArgumentsCall<*> -> executeOnParameter(call, state)
                is AtUnqualifiedNoParenthesesCall<*> -> execute(call, state)
                else -> if (Module.`is`(call) && call.isAncestor(state.get(ENTRANCE), false)) {
                    val childCalls = call.macroChildCalls()

                    for (childCall in childCalls) {
                        if (!execute(childCall, state)) {
                            break
                        }
                    }

                    keepProcessing()
                } else if (Use.`is`(call)) {
                    val useState = state.put(CallDefinitionClause.USE_CALL, call).putVisitedElement(call)

                    Use.treeWalkUp(call, useState, ::execute)
                } else {
                    true
                }
            }

    private fun execute(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>, state: ResolveState): Boolean {
        val identifierName = atUnqualifiedNoParenthesesCall.atIdentifier.identifierName()

        return if (isTypeName(identifierName) || isSpecificationName(identifierName)) {
            executeOnType(atUnqualifiedNoParenthesesCall, state)
        } else {
            true
        }
    }

    private fun execute(stabNoParenthesesSignature: ElixirStabNoParenthesesSignature, state: ResolveState): Boolean =
        executeOnChildren(stabNoParenthesesSignature .noParenthesesArguments, state)

    private fun executeOnChildren(parent: PsiElement, state: ResolveState): Boolean =
            parent
                    .firstChild
                    .siblings()
                    .filter { it.node is CompositeElement }
                    .map { execute(it, state) }
                    .takeWhile { it }
                    .lastOrNull()
                    ?: true
}
