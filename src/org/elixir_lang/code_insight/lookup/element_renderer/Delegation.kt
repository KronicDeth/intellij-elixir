package org.elixir_lang.code_insight.lookup.element_renderer

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.codeInsight.lookup.LookupElementRenderer
import org.elixir_lang.semantic.semantic

class Delegation(val name: String) : LookupElementRenderer<LookupElement>() {
    override fun renderElement(element: LookupElement, lookupElementPresentation: LookupElementPresentation) {
        lookupElementPresentation.itemText = name
        lookupElementPresentation.isItemTextBold = true

        element
            .psiElement
            ?.semantic
            ?.let { it as? org.elixir_lang.semantic.call.definition.Delegation }
            ?.let { org.elixir_lang.structure_view.element.Delegation(it) }
            ?.presentation
            ?.let {
                lookupElementPresentation.putItemPresentation(it)
            }
    }
}
