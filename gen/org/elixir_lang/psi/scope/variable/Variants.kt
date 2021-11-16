package org.elixir_lang.psi.scope.variable

import com.intellij.psi.PsiElement
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.ResolveState
import com.intellij.psi.PsiReference
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.util.PsiTreeUtil
import gnu.trove.THashMap
import org.elixir_lang.psi.impl.ElixirPsiImplUtil
import org.elixir_lang.psi.putInitialVisitedElement
import org.elixir_lang.psi.scope.Variable
import java.util.ArrayList

class Variants : Variable() {
    private var lookupElementByElement = mutableMapOf<PsiElement, LookupElement>()

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
            lookupElementByElement.computeIfAbsent(declaration) {
                LookupElementBuilder
                        .createWithSmartPointer(name, declaration)
                        .withRenderer(org.elixir_lang.code_insight.lookup.element_renderer.Variable(name))
            }
        }

        return true
    }

    private fun toList(): List<LookupElement> = lookupElementByElement.values.toList()

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
