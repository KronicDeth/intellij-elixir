package org.elixir_lang.semantic

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.psi.*
import org.elixir_lang.psi.Use
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.macroChildCallSequence
import org.elixir_lang.psi.impl.call.whileInStabBodyChildExpressions
import org.elixir_lang.psi.scope.WhileIn.whileIn

interface Quote : Modular {
    override val name: String?
        get() = null
    override val canonicalName: String?
        get() = null
    override val canonicalNameSet: Set<String>
        get() = emptySet()

    override fun elementDescription(location: ElementDescriptionLocation): String? =
        when (location) {
            UsageViewTypeLocation.INSTANCE -> "quote"
            else -> null
        }

    companion object {
        fun treeWalkUp(
            quoteCall: Call,
            resolveState: ResolveState,
            keepProcessing: (PsiElement, ResolveState) -> Boolean
        ): Boolean =
            if (!resolveState.containsAncestorUnquote(quoteCall)) {
                quoteCall
                    .macroChildCallSequence()
                    .filter { !resolveState.hasBeenVisited(it) }
                    .let { treeWalkUp(it, resolveState.putVisitedElement(quoteCall), keepProcessing) }
            } else {
                true
            }

        fun treeWalkUp(
            childCallSequence: Sequence<Call>,
            resolveState: ResolveState,
            keepProcessing: (PsiElement, ResolveState) -> Boolean
        ): Boolean {
            var accumulatorKeepProcessing = true

            for (childCall in childCallSequence) {
                val childSemantic = childCall.semantic

                accumulatorKeepProcessing = when (childSemantic) {
                    is org.elixir_lang.semantic.branching.conditional.If,
                    is org.elixir_lang.semantic.branching.conditional.Unless -> {
                        val branches = Branches(childCall)

                        val primaryKeepProcessing = whileIn(branches.primaryChildExpressions) {
                            keepProcessing(it, resolveState)
                        }

                        val alternativeKeepProcessing = whileIn(branches.alternativeChildExpressions) {
                            keepProcessing(it, resolveState)
                        }

                        primaryKeepProcessing && alternativeKeepProcessing
                    }
                    is Import -> whileIn(childSemantic.importedCallDefinitions) { definition ->
                        whileIn(definition.clauses) { clause ->
                            keepProcessing(clause.psiElement, resolveState)
                        }
                    }
                    is Unquote -> Unquote.treeWalkUp(
                        childCall,
                        resolveState,
                        keepProcessing
                    )
                    is org.elixir_lang.semantic.Use -> Use.treeWalkUp(childCall, resolveState, keepProcessing)
                    is org.elixir_lang.semantic.Try -> {
                        childCall.whileInStabBodyChildExpressions { grandChildExpression ->
                            keepProcessing(grandChildExpression, resolveState)
                        }
                    }
                    else -> keepProcessing(childCall, resolveState)
                }

                if (!accumulatorKeepProcessing) {
                    break
                }
            }

            return accumulatorKeepProcessing
        }

    }
}
