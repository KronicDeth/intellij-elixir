package org.elixir_lang.psi.scope.variable

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.ResolveState
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.impl.ElixirPsiImplUtil
import org.elixir_lang.psi.putInitialVisitedElement
import org.elixir_lang.psi.scope.Variable

class Variants : Variable() {
    private var lookupElementByName = mutableMapOf<String, LookupElement>()

    /**
     * Decides whether `match` matches the criteria being searched for.  All other [.execute] methods
     * eventually end here.
     *
     * @return `false`, as all variables should be found.  Prefix filtering will be done later by IDEA core.
     */
    override fun executeOnVariable(match: PsiNamedElement, state: ResolveState): Boolean {
        val declaration = match.reference?.resolve() ?: match
        val name = (declaration as? PsiNamedElement)?.name ?: match.name

        if (name != null) {
            // Dedup by name: a variable rebound in the same scope or shadowed across nested scopes has
            // multiple declaration sites, but the completion popup only shows the name, so each name
            // must appear once. `treeWalkUp` visits the nearest (innermost/shadowing) binding first,
            // so `computeIfAbsent` keeps that one as the navigation target.
            lookupElementByName.computeIfAbsent(name) {
                LookupElementBuilder
                        .createWithSmartPointer(name, declaration)
                        .withRenderer(org.elixir_lang.code_insight.lookup.element_renderer.Variable(name))
            }
        }

        return true
    }

    private fun toList(): List<LookupElement> = lookupElementByName.values.toList()

    companion object {
        fun lookupElementList(entrance: PsiElement): List<LookupElement> {
            val variants = Variants()

            PsiTreeUtil.treeWalkUp(
                    variants,
                    entrance,
                    entrance.containingFile,
                    ResolveState
                            .initial()
                            .put(ElixirPsiImplUtil.ENTRANCE, entrance)
                            .putInitialVisitedElement(entrance)
            )

            return variants.toList()
        }
    }
}
