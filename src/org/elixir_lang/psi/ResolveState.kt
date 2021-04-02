package org.elixir_lang.psi

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState

private val VISITED_ELEMENT_SET = Key<Set<PsiElement>>("VISITED_ELEMENTS")

fun <T : PsiElement> T.takeUnlessHasNotBeenVisited(state: ResolveState): T? = takeUnless { state.hasBeenVisited(it) }

fun ResolveState.hasBeenVisited(element: PsiElement): Boolean = this.get(VISITED_ELEMENT_SET).contains(element)

fun ResolveState.putInitialVisitedElement(visitedElement: PsiElement): ResolveState {
    assert(this.get(VISITED_ELEMENT_SET) == null) {
        "VISITED_ELEMENT_SET already populated"
    }

    return this.put(VISITED_ELEMENT_SET, setOf(visitedElement))
}

fun ResolveState.putVisitedElement(visitedElement: PsiElement): ResolveState {
    val visitedElementSet = this.get(VISITED_ELEMENT_SET)

    return this.put(VISITED_ELEMENT_SET, visitedElementSet + setOf(visitedElement))
}
