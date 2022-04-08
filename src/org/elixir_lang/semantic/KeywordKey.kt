package org.elixir_lang.semantic

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.psi.ElixirKeywordKey

class KeywordKey(val elixirKeywordKey: ElixirKeywordKey) : Semantic {
    override val psiElement: PsiElement
        get() = elixirKeywordKey
    val name: String by lazy {
        elixirKeywordKey.name ?: "?"
    }

    override fun elementDescription(location: ElementDescriptionLocation): String? =
        when (location) {
            UsageViewTypeLocation.INSTANCE -> "keyword key"
            else -> null
        }
}
