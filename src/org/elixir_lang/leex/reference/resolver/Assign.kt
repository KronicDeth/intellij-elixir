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
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.operation.Arrow
import org.elixir_lang.psi.operation.Match

object Assign: ResolveCache.PolyVariantResolver<org.elixir_lang.leex.reference.Assign> {
    override fun resolve(assign: Assign, incompleteCode: Boolean): Array<ResolveResult> =
        assign.element.containingFile.context
                ?.let { it as? Call }
                ?.let { viewModule -> resolveInViewModule(assign, incompleteCode, viewModule) }
                .orEmpty()
                .toTypedArray()

    private fun resolveInViewModule(assign: Assign, incompleteCode: Boolean, viewModule: Call): List<ResolveResult> =
        callDefinitionClauseCallFoldWhile(viewModule, emptyList<ResolveResult>()) { callDefinitionClauseCall, acc ->
            // Always continue because there is no way to determine which definition is called last if the view module has enough helper functions.
            AccumulatorContinue(resolveInCallDefinitionClause(assign, incompleteCode, callDefinitionClauseCall, acc), true)
        }.accumulator

    private fun resolveInCallDefinitionClause(assign: Assign, incompleteCode: Boolean, callDefinitionClause: Call, initial: List<ResolveResult>): List<ResolveResult> =
            callDefinitionClause
                    .doBlock
                    ?.stab
                    ?.stabBody
                    ?.let { resolveInCallDefinitionClauseExpression(assign, incompleteCode, it, initial) }
                    ?: initial

    private fun resolveInCallDefinitionClauseExpression(assign: Assign, incompleteCode: Boolean, expression: PsiElement, initial: List<ResolveResult>): List<ResolveResult> =
            when (expression) {
                is ElixirAccessExpression, is ElixirStabBody, is ElixirTuple -> expression
                        .lastChild
                        .siblings(forward = false, withSelf = true)
                        .filter { it.node is CompositeElement }
                        .fold(initial) { acc, child ->
                            resolveInCallDefinitionClauseExpression(assign, incompleteCode, child, acc)
                        }
                is ElixirAtom, is ElixirEndOfExpression, is None -> null
                is Arrow -> {
                    val leftAcc = expression.leftOperand()?.let { leftOperand ->
                        resolveInCallDefinitionClauseExpression(assign, incompleteCode, leftOperand, initial)
                    } ?: initial

                    expression.rightOperand()?.let { rightOperand ->
                        resolveInCallDefinitionClauseExpression(assign, incompleteCode, rightOperand, leftAcc)
                    } ?: leftAcc
                }
                is Match -> expression.rightOperand()?.let { rightOperand ->
                    resolveInCallDefinitionClauseExpression(assign, incompleteCode, rightOperand, initial)
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
                                expression
                                        .finalArguments()
                                        ?.let { finalArguments -> finalArguments[finalArguments.size - 2] }
                                        ?.let { resolveInAssign3Argument(assign, incompleteCode, it, initial) }
                            }
                            else -> null
                        }
                    } else {
                        null
                    }
                else -> {
                    TODO()
                }
            } ?: initial

    private fun resolveInAssign2Argument(assign: Assign, incompleteCode: Boolean, expression: PsiElement, initial: List<ResolveResult>): List<ResolveResult> =
        when (expression) {
            is ElixirKeywordKey -> {
                if (expression.charListLine == null && expression.stringLine == null) {
                    val resolvedName = expression.text
                    val assignName = assign.name

                    if (resolvedName.startsWith(assignName)) {
                        val validResult = resolvedName == assignName

                        initial + listOf(PsiElementResolveResult(expression, validResult))
                    } else {
                        null
                    }
                } else {
                    null
                } ?: initial
            }
            is QuotableKeywordList -> {
                expression.quotableKeywordPairList().fold(initial) { acc, keywordPair ->
                    resolveInAssign2Argument(assign, incompleteCode, keywordPair, acc)
                }
            }
            is QuotableKeywordPair -> resolveInAssign2Argument(assign, incompleteCode, expression.keywordKey, initial)
            else -> {
                TODO()
            }
        }

    private fun resolveInAssign3Argument(assign: Assign, incompleteCode: Boolean, expression: PsiElement, initial: List<ResolveResult>): List<ResolveResult> =
            when (expression) {
                is ElixirAccessExpression -> resolveInAssign3Argument(assign, incompleteCode, expression.stripAccessExpression(), initial)
                is ElixirAtom -> if (expression.charListLine == null && expression.stringLine == null) {
                    val resolvedName = expression.node.lastChildNode.text
                    val assignName = assign.name

                    if (resolvedName.startsWith(assignName)) {
                        val validResult = resolvedName == assignName
                        initial + listOf(PsiElementResolveResult(expression, validResult))
                    } else {
                        null
                    }
                } else {
                    null
                } ?: initial
                else -> {
                    TODO()
                }
            }
}
