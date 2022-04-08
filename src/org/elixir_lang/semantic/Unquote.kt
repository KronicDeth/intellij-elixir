package org.elixir_lang.semantic

import com.intellij.psi.*
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.operation.Match
import org.elixir_lang.psi.scope.WhileIn
import org.elixir_lang.semantic.call.definition.Clause
import kotlin.collections.List

interface Unquote : Semantic {
    override fun elementDescription(location: ElementDescriptionLocation): String? =
        when (location) {
            UsageViewTypeLocation.INSTANCE -> "unquote"
            else -> null
        }

    companion object {
        fun treeWalkUp(
            unquoteCall: Call,
            resolveState: ResolveState,
            keepProcessing: (PsiElement, ResolveState) -> Boolean
        ): Boolean {
            val unquoteCallResolveState = resolveState.putVisitedElement(unquoteCall)

            return unquoteCall
                .finalArguments()
                ?.singleOrNull()
                ?.let { it as? Call }
                ?.takeUnlessHasBeenVisited(unquoteCallResolveState)
                ?.reference
                ?.let { it as PsiPolyVariantReference }
                ?.let { reference -> treeWalkUp(reference, unquoteCallResolveState, keepProcessing) }
                ?: true
        }

        fun isQualified(qualified: Qualified): Boolean =
            isQualified(qualified, qualified.functionName())

        fun isQualified(qualified: Qualified, name: String?): Boolean =
            name == Function.UNQUOTE &&
                    qualified.resolvedPrimaryArity() == 1 &&
                    qualified.enclosingModular is Quote

        fun ancestorUnquote(descendent: PsiElement): Call? =
            descendent
                .parent
                ?.parent
                ?.parent?.semantic?.let { it as? org.elixir_lang.semantic.unquote.Call }?.call

        private fun treeWalkUp(
            reference: PsiPolyVariantReference,
            resolveState: ResolveState,
            keepProcessing: (PsiElement, ResolveState) -> Boolean
        ): Boolean =
            reference
                .multiResolve(false)
                .filter(ResolveResult::isValidResult)
                .mapNotNull(ResolveResult::getElement)
                .filterIsInstance<Call>()
                .let { resolveds -> treeWalkUpUnquoted(resolveds, resolveState, keepProcessing) }

        private fun treeWalkUpUnquoted(
            unquotedList: List<Call>,
            resolveState: ResolveState,
            keepProcessing: (PsiElement, ResolveState) -> Boolean
        ): Boolean =
            WhileIn.whileIn(unquotedList) { unquoted ->
                if (unquoted.semantic is Clause) {
                    Using.treeWalkUp(unquoted, null, resolveState, keepProcessing)
                } else {
                    treeWalkUpUnquotedVariable(unquoted, resolveState, keepProcessing)
                    // a variable

                    true
                }
            }

        private tailrec fun treeWalkUpUnquotedVariable(
            unquoted: PsiElement,
            resolveState: ResolveState,
            keepProcessing: (PsiElement, ResolveState) -> Boolean
        ): Boolean =
            when (val parent = unquoted.parent) {
                is Match -> {
                    // variable = ...
                    if (parent.leftOperand() == unquoted) {
                        parent.rightOperand()?.let { value ->
                            treeWalkUpValue(value, resolveState, keepProcessing)
                        } ?: true
                    }
                    // ... = variable
                    else {
                        Logger.error(
                            Unquote::class.java,
                            "Don't know how to walk unquoted variable parent",
                            parent
                        )

                        true
                    }
                }
                // ...: variable
                is ElixirKeywordPair -> if (parent.keywordKey.name == "do") {
                    // `do: variable`, such as `do: block` in macro parameters
                    true
                } else {
                    Logger.error(
                        Unquote::class.java,
                        "Don't know how to walk unquoted variable parent",
                        parent
                    )

                    true
                }
                // (..., parameter)
                is ElixirParenthesesArguments -> true
                is ElixirTuple,
                is ElixirAccessExpression -> treeWalkUpUnquotedVariable(parent, resolveState, keepProcessing)
                else -> {
                    Logger.error(
                        Unquote::class.java,
                        "Don't know how to walk unquoted variable parent",
                        parent
                    )

                    true
                }
            }

        private fun treeWalkUpValue(
            value: PsiElement,
            resolveState: ResolveState,
            keepProcessing: (PsiElement, ResolveState) -> Boolean
        ): Boolean =
            when (value) {
                is Call -> treeWalkUpValue(value, resolveState, keepProcessing)
                else -> {
                    Logger.error(
                        Unquote::class.java,
                        "Don't know how to walk unquoted variable value",
                        value
                    )

                    true
                }
            }

        private fun treeWalkUpValue(
            value: Call,
            resolveState: ResolveState,
            keepProcessing: (PsiElement, ResolveState) -> Boolean
        ): Boolean =
            when {
                value.semantic is org.elixir_lang.semantic.quote.Call -> Quote.treeWalkUp(
                    value, resolveState,
                    keepProcessing
                )
                Case.`is`(value) -> Case.treeWalkUp(value, resolveState, keepProcessing)
                else -> {
                    value.reference?.let { it as PsiPolyVariantReference }?.let { reference ->
                        treeWalkUp(reference, resolveState.putVisitedElement(value), keepProcessing)
                    } ?: true
                }
            }
    }
}
