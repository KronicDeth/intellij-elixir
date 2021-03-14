package org.elixir_lang.leex.reference.resolver

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.util.siblings
import org.elixir_lang.leex.reference.Assign
import org.elixir_lang.psi.*
import org.elixir_lang.psi.Modular.callDefinitionClauseCallFoldWhile
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.arguments.None
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.operation.Match

object Assign: ResolveCache.PolyVariantResolver<org.elixir_lang.leex.reference.Assign> {
    override fun resolve(assign: Assign, incompleteCode: Boolean): Array<ResolveResult> =
        assign.element.containingFile.context
                ?.let { it as? Call }
                ?.let { viewModule -> resolveInViewModule(assign, incompleteCode, viewModule) }
                .orEmpty()
                .toTypedArray()

    private fun resolveInViewModule(assign: Assign, incompleteCode: Boolean, viewModule: Call): List<ResolveResult> =
        callDefinitionClauseCallFoldWhile(viewModule, "update", emptyList<ResolveResult>()) { callDefinitionClauseCall, name, arityRange, acc ->
            if (arityRange.contains(2)) {
                resolveInUpdate(assign, incompleteCode, callDefinitionClauseCall, acc)
            } else {
                AccumulatorContinue(acc, true)
            }
        }.accumulator

    private fun resolveInUpdate(assign: Assign, incompleteCode: Boolean, updateClause: Call, initial: List<ResolveResult>): AccumulatorContinue<List<ResolveResult>> =
            updateClause
                    .doBlock
                    ?.stab
                    ?.stabBody
                    ?.let { resolveInUpdateExpression(assign, incompleteCode, it, initial) }
                    ?: AccumulatorContinue(initial, true)

    private fun resolveInUpdateExpression(assign: Assign, incompleteCode: Boolean, expression: PsiElement, initial: List<ResolveResult>): AccumulatorContinue<List<ResolveResult>> =
            when (expression) {
                is ElixirAccessExpression, is ElixirStabBody, is ElixirTuple -> expression
                        .lastChild
                        .siblings(forward = false, withSelf = true)
                        .filter { it.node is CompositeElement }
                        .let { childSequence ->
                            var accumulatorContinue = AccumulatorContinue(initial, true)

                            for (child in childSequence) {
                                accumulatorContinue = resolveInUpdateExpression(assign, incompleteCode, child, accumulatorContinue.accumulator)

                                if (!accumulatorContinue.`continue`) {
                                    break
                                }
                            }

                            accumulatorContinue
                        }
                is ElixirAtom, is ElixirEndOfExpression, is None -> null
                is Match -> expression.rightOperand()?.let { rightOperand ->
                    resolveInUpdateExpression(assign, incompleteCode, rightOperand, initial)
                }
                // After `None` as `assign/2` and `assign/3` have options
                // After `Match` because it is a more specific `Call`
                is Call ->
                    if (expression.functionName() == "assign") {
                        when (expression.resolvedFinalArity()) {
                            2 -> {
                                expression
                                        .finalArguments()
                                        ?.last()
                                        ?.let { resolveInAssign2Argument(assign, incompleteCode, it, initial) }
                            }
                            3 -> {
                                TODO()
                            }
                            else -> null
                        }
                    } else {
                        null
                    }
                else -> {

                    TODO()
                }
            } ?: AccumulatorContinue(initial, true)

    private fun resolveInAssign2Argument(assign: Assign, incompleteCode: Boolean, expression: PsiElement, initial: List<ResolveResult>): AccumulatorContinue<List<ResolveResult>> =
        when (expression) {
            is ElixirKeywordKey -> {
                if (expression.charListLine == null && expression.stringLine == null) {
                    val resolvedName = expression.text
                    val assignName = assign.name

                    if (resolvedName.startsWith(assignName)) {
                        val validResult = resolvedName == assignName
                        val final = initial + listOf(PsiElementResolveResult(expression, validResult))

                        AccumulatorContinue(final, !validResult)
                    } else {
                        null
                    }
                } else {
                    null
                } ?: AccumulatorContinue(initial, true)
            }
            is QuotableKeywordList -> {
                var accumulatorContinue = AccumulatorContinue(initial, true)

                for (keywordPair in expression.quotableKeywordPairList()) {
                    accumulatorContinue = resolveInAssign2Argument(assign, incompleteCode, keywordPair, accumulatorContinue.accumulator)

                    if (!accumulatorContinue.`continue`) {
                        break
                    }
                }

                accumulatorContinue
            }
            is QuotableKeywordPair -> resolveInAssign2Argument(assign, incompleteCode, expression.keywordKey, initial)
            else -> {
                TODO()
            }
        }
}
