package org.elixir_lang.semantic.variable

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.psi.ElixirKeywordKey
import org.elixir_lang.semantic.Variable

class QuoteBound(override val psiElement: ElixirKeywordKey) : Variable {
    override val name: String by lazy {
        TODO()
    }

    override fun elementDescription(location: ElementDescriptionLocation): String? =
        when (location) {
            UsageViewTypeLocation.INSTANCE -> "quote bound variable"
            else -> null
        }
}
