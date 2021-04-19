package org.elixir_lang.ecto

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveState
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.operation.In
import org.elixir_lang.psi.scope.WhileIn.whileIn
import org.elixir_lang.structure_view.element.CallDefinitionClause.Companion.enclosingModularMacroCall

object Query {
    fun isDeclaringMacro(call: Call, state: ResolveState): Boolean =
        call.functionName()?.let { functionName ->
            when (functionName) {
                FROM -> call.resolvedFinalArity() == FROM_ARITY && resolvesToEctoQuery(call, state)
                else -> false
            }
        } ?: false

    private fun resolvesToEctoQuery(call: Call, state: ResolveState): Boolean =
        // it is not safe to call `multiResolve` on the call's reference if that `call` is currently being resolved.
        if (!call.isEquivalentTo(state.get(ENTRANCE))) {
            call.reference.let { it as PsiPolyVariantReference }.multiResolve(false).any { resolveResult ->
                if (resolveResult.isValidResult) {
                    resolveResult.element?.let { it as? Call }?.let { resolved ->
                        CallDefinitionClause.isMacro(resolved) && enclosingModularMacroCall(resolved)?.name  == "Ecto.Query"
                    } ?: false
                } else {
                    false
                }
            }
        } else {
            false
        }

    fun treeWalkUp(call: Call, state: ResolveState, keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
            call.functionName()?.let { functionName ->
                when (functionName) {
                    FROM -> {
                        when (call.resolvedFinalArity()) {
                            FROM_ARITY -> executeOnFrom(call, state, keepProcessing)
                            else -> true
                        }
                    }
                    else -> true
                }
            } ?: true

    fun executeOnFrom(call: Call, state: ResolveState, keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
        call.finalArguments()?.let { finalArguments ->
            executeOnIn(finalArguments[0], state, keepProcessing) && executeOnFromKeywords(finalArguments[1], state, keepProcessing)
        } ?: true

    fun executeOnIn(fromIn: PsiElement,
                    state: ResolveState,
                    keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
        //  If the query needs a reference to the data source in any other part of the expression, then an in must be
        //  used to create a reference variable.
        fromIn.let { it as? In }?.leftOperand()?.let { executeOnInReference(it, state, keepProcessing) } ?: true

    private fun executeOnInReference(
            inReference: PsiElement,
            state: ResolveState,
            keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
        when (inReference) {
            is UnqualifiedNoArgumentsCall<*> -> if (inReference.resolvedFinalArity() == 0) {
                keepProcessing(inReference, state)
            } else {
                true
            }
            else -> {
                Logger.error(logger, "Don't know how to find reference variables in Ecto query", inReference)

                true
            }
        }

    fun executeOnFromKeywords(fromKeywords: PsiElement,
                              state: ResolveState,
                              keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
        when (fromKeywords) {
            is QuotableKeywordList -> {
                whileIn(fromKeywords.quotableKeywordPairList()) { quotableKeywordPair ->
                    executeOnFromKeywords(quotableKeywordPair, state, keepProcessing)
                }
            }
            is QuotableKeywordPair -> {
                when (val keywordKeyText = fromKeywords.keywordKey.text) {
                    "cross_join", "full_join", "inner_join", "inner_lateral_join", "join", "left_join",
                    "left_lateral_join", "right_join" -> executeOnIn(fromKeywords.keywordValue, state, keepProcessing)
                    // Cannot declare a reference variable
                    "as", "distinct", "group_by", "on", "order_by", "select", "where" -> true
                    else -> {
                        Logger.error(logger, "Don't know how to find reference variables for keyword key $keywordKeyText", fromKeywords)

                        true
                    }
                }
            }
            else -> {
                Logger.error(logger, "Don't kow how to find reference variables in keyword arguments of from/2", fromKeywords)

                true
            }
        }

    private const val FROM = "from"
    private const val FROM_ARITY = 2

    private val logger by lazy { com.intellij.openapi.diagnostic.Logger.getInstance(Query::class.java) }
}
