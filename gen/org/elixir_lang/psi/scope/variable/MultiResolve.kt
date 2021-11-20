package org.elixir_lang.psi.scope.variable

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.Key
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.ex_unit.Assertions
import org.elixir_lang.psi.impl.ElixirPsiImplUtil
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.previousSiblingExpression
import org.elixir_lang.psi.impl.ProcessDeclarationsImpl.DECLARING_SCOPE
import org.elixir_lang.psi.operation.Match
import org.elixir_lang.psi.scope.MultiResolve.keepProcessing
import org.elixir_lang.psi.scope.Variable
import org.elixir_lang.psi.scope.VisitedElementSetResolveResult
import org.elixir_lang.psi.stub.index.QuoteVariableName
import org.elixir_lang.reference.Callable
import org.jetbrains.annotations.Contract
import java.util.*

class MultiResolve(private val name: String, private val incompleteCode: Boolean) : Variable() {
    private val resolveResultList = mutableListOf<VisitedElementSetResolveResult>()

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
                            previousExpression(matchAncestor, state)?.let { expression ->
                                val preboundResolveResultList = resolveInScope(
                                        name,
                                        incompleteCode,
                                        expression,
                                        ResolveState
                                                .initial()
                                                .put(ElixirPsiImplUtil.ENTRANCE, matchAncestor)
                                                .putInitialVisitedElement(matchAncestor)
                                                .putVisitedElements(state)
                                                .put(LAST_BINDING_KEY, element)
                                )

                                if (!preboundResolveResultList.isNotEmpty()) {
                                    if (!incompleteCode && validResult) {
                                        val validPreboundResolveResultList =  preboundResolveResultList.filter { it.isValidResult }

                                        if (validPreboundResolveResultList.isNotEmpty()) {
                                            resolveResultList.addAll(validPreboundResolveResultList)
                                            added = true
                                        }
                                    } else {
                                        resolveResultList.addAll(preboundResolveResultList)
                                        added = true
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // either non-right match declaration or recursive call didn't find a rebinding
            if (!added) {
                resolveResultList.add(VisitedElementSetResolveResult(element, validResult, state.visitedElementSet()))
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
                              entrance: PsiElement): List<VisitedElementSetResolveResult> =
            if (name == Callable.IGNORED) {
                listOf<VisitedElementSetResolveResult>(VisitedElementSetResolveResult(entrance, true, emptySet()))
            } else {
                val resolveState = ResolveState.initial().put(ENTRANCE, entrance).putInitialVisitedElement(entrance)

                resolveInScope(name, incompleteCode, entrance, resolveState)
                        .takeIf { set -> set.any(ResolveResult::isValidResult) }
                        ?: nameInAnyQuote(entrance, name, incompleteCode)
            }

        fun resolveInScope(name: String,
                           incompleteCode: Boolean,
                           entrance: PsiElement,
                           resolveState: ResolveState): List<VisitedElementSetResolveResult> {
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

        private fun nameInAnyQuote(entrance: PsiElement,
                                   name: String,
                                   incompleteCode: Boolean): List<VisitedElementSetResolveResult> {
            val project = entrance.project
            val resolveResults = mutableListOf<VisitedElementSetResolveResult>()

            if (!DumbService.isDumb(project)) {
                val stubIndex = StubIndex.getInstance()
                val keys = mutableListOf<String>()

                stubIndex.processAllKeys(QuoteVariableName.KEY, project) { key ->
                    if ((incompleteCode && key.startsWith(name)) || key == name) {
                        keys.add(key)
                    }

                    true
                }

                val scope = GlobalSearchScope.allScope(project)
                // results are never valid because the qualifier is unknown
                val validResult = false

                for (key in keys) {
                    stubIndex
                            .processElements(QuoteVariableName.KEY, key, project, scope, NamedElement::class.java) { namedElement ->
                                resolveResults.add(VisitedElementSetResolveResult(namedElement, validResult, emptySet()))

                                true
                            }
                }
            }

            return resolveResults
        }

        @Contract(pure = true)
        private fun previousExpression(element: PsiElement, state: ResolveState): PsiElement? =
            previousSiblingExpression(element) ?: previousParentExpression(element, state)

        @Contract(pure = true)
        private fun previousParentExpression(element: PsiElement, state: ResolveState): PsiElement? {
            var expression = element
            do {
                expression = expression.parent
            } while (expression is Arguments ||
                    expression is ElixirDoBlock ||
                    expression is ElixirStab ||
                    expression is ElixirStabBody || (expression is Call && Assertions.isChild(expression, state)))
            return expression
        }
    }
}
