package org.elixir_lang.psi.scope.variable

import com.intellij.openapi.util.Key
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.*
import org.elixir_lang.psi.impl.ElixirPsiImplUtil
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.previousSiblingExpression
import org.elixir_lang.psi.impl.ProcessDeclarationsImpl.DECLARING_SCOPE
import org.elixir_lang.psi.operation.Match
import org.elixir_lang.psi.scope.MultiResolve.keepProcessing
import org.elixir_lang.psi.scope.Variable
import org.elixir_lang.reference.Callable
import org.jetbrains.annotations.Contract
import java.util.*

class MultiResolve(private val name: String, private val incompleteCode: Boolean) : Variable() {
    private val resolveResultList = mutableListOf<ResolveResult>()

    /**
     * Decides whether `match` matches the criteria being searched for.  All other [.executeOnVariable] methods
     * eventually end here.
     *
     * @param match
     * @param state
     */
    override fun executeOnVariable(match: PsiNamedElement, state: ResolveState): Boolean {
        addToResolveResultListIfMatchingName(match, state)
        return keepProcessing(incompleteCode, resolveResultList)
    }

    private fun addToResolveResultList(element: PsiElement, state: ResolveState, validResult: Boolean) {
        val declaringScope = state.get<Boolean>(DECLARING_SCOPE)

        if (declaringScope == null || declaringScope) {
            val lastBinding = state.get(LAST_BINDING_KEY)
            var added = false

            /* if LAST_BINDING_KEY is set, then we're checking if a right-hand match is bound higher up, so an effective
               recursive call.  If the recursive call got the same result, stop the recursion by not checking for
               rebinding */
            if (lastBinding == null || !element.isEquivalentTo(lastBinding)) {
                PsiTreeUtil.getContextOfType(element, Match::class.java)?.let { matchAncestor ->
                    matchAncestor.rightOperand()?.let { rightOperand ->
                        /* right-hand match can only be declarative if it is not already bound, so need to try to
                           resolve further up to try to find if {@code element} is already bound */
                        if (PsiTreeUtil.isAncestor(rightOperand, element, false)) {
                            // previous sibling or parent to search for earlier binding
                            previousExpression(matchAncestor)?.let { expression ->
                                val preboundResolveResultList = resolveResultList(
                                        name,
                                        incompleteCode,
                                        expression,
                                        ResolveState
                                                .initial()
                                                .put(ElixirPsiImplUtil.ENTRANCE, matchAncestor)
                                                .putInitialVisitedElement(matchAncestor)
                                                .put(LAST_BINDING_KEY, element)
                                )

                                if (preboundResolveResultList.isNotEmpty()) {
                                    resolveResultList.addAll(preboundResolveResultList)
                                    added = true
                                }
                            }
                        }
                    }
                }
            }

            // either non-right match declaration or recursive call didn't find a rebinding
            if (!added) {
                resolveResultList.add(PsiElementResolveResult(element, validResult))
            }
        }
    }

    private fun addToResolveResultListIfMatchingName(match: PsiNamedElement, state: ResolveState) {
        match.name?.let { name ->
            if (name.startsWith(this.name)) {
                val validResult = name == this.name
                addToResolveResultList(match, state, validResult)
            }
        }
    }

    companion object {
        private val LAST_BINDING_KEY = Key<PsiElement>("LAST_BINDING_KEY")

        fun resolveResultList(name: String,
                              incompleteCode: Boolean,
                              entrance: PsiElement): List<ResolveResult> =
            if (name == Callable.IGNORED) {
                listOf<ResolveResult>(PsiElementResolveResult(entrance))
            } else {
                val resolveState = ResolveState.initial().put(ENTRANCE, entrance).putInitialVisitedElement(entrance)

                resolveResultList(name, incompleteCode, entrance, resolveState)
            }

        fun resolveResultList(name: String,
                              incompleteCode: Boolean,
                              entrance: PsiElement,
                              resolveState: ResolveState): List<ResolveResult> {
            val multiResolve = MultiResolve(name, incompleteCode)

            val treeWalkUpResolveState = if (resolveState.get(ENTRANCE) == null) {
                resolveState.put(ENTRANCE, entrance)
            } else {
                resolveState
            }

            PsiTreeUtil.treeWalkUp(
                    multiResolve,
                    entrance,
                    entrance.containingFile,
                    treeWalkUpResolveState
            )

            return multiResolve.resolveResultList
        }

        @Contract(pure = true)
        private fun previousExpression(element: PsiElement): PsiElement? =
            previousSiblingExpression(element) ?: previousParentExpression(element)

        @Contract(pure = true)
        private fun previousParentExpression(element: PsiElement): PsiElement? {
            var expression = element
            do {
                expression = expression.parent
            } while (expression is Arguments ||
                    expression is ElixirDoBlock ||
                    expression is ElixirStab ||
                    expression is ElixirStabBody)
            return expression
        }
    }
}
