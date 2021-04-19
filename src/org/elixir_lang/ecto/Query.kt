package org.elixir_lang.ecto

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveState
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.util.isAncestor
import com.intellij.psi.util.siblings
import org.elixir_lang.NameArityRange
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.operation.In
import org.elixir_lang.psi.scope.WhileIn.whileIn
import org.elixir_lang.structure_view.element.CallDefinitionClause.Companion.enclosingModularMacroCall
import org.elixir_lang.safeMultiResolve

object Query {
    fun isDeclaringMacro(call: Call, state: ResolveState): Boolean =
        call.functionName()?.let { functionName ->
            when (functionName) {
                JOIN_NAME_ARITY_RANGE.name -> call.resolvedFinalArity() in JOIN_NAME_ARITY_RANGE.arityRange && resolvesToEctoQuery(call, state)
                FROM_NAME_ARITY_RANGE.name -> call.resolvedFinalArity() in FROM_NAME_ARITY_RANGE.arityRange && resolvesToEctoQuery(call, state)
                else -> false
            }
        } ?: false

    private fun resolvesToEctoQuery(call: Call, state: ResolveState): Boolean =
        // it is not safe to call `multiResolve` on the call's reference if that `call` is currently being resolved.
        if (!isBeingResolved(call, state)) {
            val reference = call.reference as PsiPolyVariantReference

            safeMultiResolve(reference, false).any { resolveResult ->
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

    private fun isBeingResolved(call: Call, state: ResolveState): Boolean =
        call.isEquivalentTo(state.get(ENTRANCE)) || qualifierIsBeingResolved(call, state)

    private fun qualifierIsBeingResolved(call: Call, state: ResolveState): Boolean =
        if (call is Qualified) {
            call.qualifier().isAncestor(state.get(ENTRANCE), strict = false)
        } else {
            false
        }

    fun treeWalkUp(call: Call, state: ResolveState, keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
            call.functionName()?.let { functionName ->
                when (functionName) {
                    FROM_NAME_ARITY_RANGE.name ->
                        if (call.resolvedFinalArity() in FROM_NAME_ARITY_RANGE.arityRange) {
                            executeOnFrom(call, state, keepProcessing)
                        } else {
                            true
                        }
                    JOIN_NAME_ARITY_RANGE.name ->
                        if (call.resolvedFinalArity() in JOIN_NAME_ARITY_RANGE.arityRange) {
                            executeOnJoin(call, state, keepProcessing)
                        } else {
                            true
                        }
                    else -> true
                }
            } ?: true

    fun executeOnFrom(call: Call, state: ResolveState, keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
        call.finalArguments()?.let { finalArguments ->
            executeOnIn(finalArguments[0], state, keepProcessing) &&
                    ((finalArguments.size < 2) ||
                            executeOnFromKeywords(finalArguments[1], state, keepProcessing))
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
            is UnqualifiedNoArgumentsCall<*> ->
                inReference.resolvedFinalArity() != 0 || keepProcessing(inReference, state)
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

    fun executeOnJoin(call: Call,
                      state: ResolveState,
                      keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean? =
        call.finalArguments()?.let { arguments ->
            // `join(query, qual, binding \\ [], expr, opts \\ [])`
            when (call.resolvedFinalArity()) {
                // `join(query, qual, expr)` or `|> join(qual, expr)`
                3 -> executeOnIn(arguments[arguments.lastIndex], state, keepProcessing)
                // `join(query, qual, binding, expr)` or `|> join(qual, binding, expr)`
                4 -> executeOnBinding(arguments[arguments.lastIndex - 1], state, keepProcessing) &&
                        executeOnIn(arguments[arguments.lastIndex], state, keepProcessing)
                // `join(query, qual, binding, expr, opts)` or `|> join(qual, binding, expr, opts)`
                5 -> executeOnBinding(arguments[arguments.lastIndex - 2], state, keepProcessing) &&
                        executeOnIn(arguments[arguments.lastIndex - 1], state, keepProcessing)
                else -> {
                    Logger.error(logger, "join arity outside of range (3..5)", call)
                    true
                }
            }
        }

    private tailrec fun executeOnBinding(
            element: PsiElement,
            state: ResolveState,
            keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
        when (element) {
            is ElixirAccessExpression -> executeOnBinding(element.stripAccessExpression(), state, keepProcessing)
            is ElixirList -> {
                val elements = element.lastChild.siblings(forward = false).filter { it.node is CompositeElement }

                whileIn(elements) { executeOnBinding(it, state, keepProcessing) }
            }
            is QuotableKeywordList -> {
                val keywordValues = element.quotableKeywordPairList().mapNotNull(QuotableKeywordPair::getKeywordValue)

                whileIn(keywordValues) {
                    executeOnBinding(it, state, keepProcessing)
                }
            }
            is UnqualifiedNoArgumentsCall<*> -> element.resolvedFinalArity() != 0 || keepProcessing(element, state)
            else -> {
                Logger.error(logger, "Don't know how to find reference variables in binding", element)

                true
            }
        }

    private val FROM_NAME_ARITY_RANGE = NameArityRange("from", 1..2)
    private val JOIN_NAME_ARITY_RANGE = NameArityRange("join", 3..5)

    private val logger by lazy { com.intellij.openapi.diagnostic.Logger.getInstance(Query::class.java) }
}
