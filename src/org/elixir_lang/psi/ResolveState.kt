package org.elixir_lang.psi

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState

private val VISITED_ELEMENT_SET = Key<Set<PsiElement>>("VISITED_ELEMENTS")

fun <T : PsiElement> T.takeUnlessHasBeenVisited(state: ResolveState): T? = takeUnless { state.hasBeenVisited(it) }

fun ResolveState.hasBeenVisited(element: PsiElement): Boolean = this.get(VISITED_ELEMENT_SET).contains(element)

fun ResolveState.putInitialVisitedElement(visitedElement: PsiElement): ResolveState {
    assert(this.get(VISITED_ELEMENT_SET) == null) {
        "VISITED_ELEMENT_SET already populated"
    }

    return this.put(VISITED_ELEMENT_SET, setOf(visitedElement))
}

fun ResolveState.putVisitedElement(visitedElement: PsiElement): ResolveState {
    val visitedElementSet = this.get(VISITED_ELEMENT_SET) ?: emptySet()

    if (this.get(VISITED_ELEMENT_SET) == null) {
       Logger.getInstance(ResolveState::class.java).error("VISITED_ELEMENT_SET is null.  putInitialVisitedElement was not called")
    }

    return this.put(VISITED_ELEMENT_SET, visitedElementSet + setOf(visitedElement))
}
