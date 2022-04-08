package org.elixir_lang.semantic

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.usageView.UsageViewNodeTextLocation
import com.intellij.usageView.UsageViewShortNameLocation
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.psi.ElixirAtom

class Atom(val elixirAtom: ElixirAtom) : Semantic {
    override val psiElement: PsiElement
        get() = elixirAtom

    override fun elementDescription(location: ElementDescriptionLocation): String? =
        when (location) {
            UsageViewNodeTextLocation.INSTANCE, UsageViewShortNameLocation.INSTANCE -> psiElement.text
            UsageViewTypeLocation.INSTANCE -> "atom"
            else -> null
        }
}
