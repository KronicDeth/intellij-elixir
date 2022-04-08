package org.elixir_lang.code_insight.lookup.element_renderer

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.codeInsight.lookup.LookupElementRenderer
import org.elixir_lang.semantic.semantic

class EExFunctionFrom(val name: String) : LookupElementRenderer<LookupElement>() {
    override fun renderElement(element: LookupElement, lookupElementPresentation: LookupElementPresentation) {
        lookupElementPresentation.itemText = name
        lookupElementPresentation.isItemTextBold = true

        element
            .psiElement
            ?.semantic
            ?.let { it as? org.elixir_lang.semantic.call.definition.eex.FunctionFrom }
            ?.let { functionFrom ->
                org.elixir_lang.structure_view.element.EExFunctionFrom(
                    functionFrom.enclosingModular.structureViewTreeElement,
                    functionFrom
                )
            }
            ?.children
            ?.first()
            ?.presentation
            ?.also { itemPresentation ->
                lookupElementPresentation.putItemPresentation(itemPresentation)
            }
    }
}
