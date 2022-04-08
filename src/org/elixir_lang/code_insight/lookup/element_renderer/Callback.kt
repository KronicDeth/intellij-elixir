package org.elixir_lang.code_insight.lookup.element_renderer

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.codeInsight.lookup.LookupElementRenderer
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.semantic
import org.elixir_lang.semantic.type.definition.source.Callback

class Callback(val name: String) : LookupElementRenderer<LookupElement>() {
    override fun renderElement(element: LookupElement, lookupElementPresentation: LookupElementPresentation) {
        lookupElementPresentation.itemText = name
        lookupElementPresentation.isItemTextBold = true

        element
            .psiElement
            ?.let { it as? Call }
            ?.let { call ->
                call
                    .semantic?.let { it as? Callback }
                    ?.let { semantic ->
                        org.elixir_lang.structure_view.element.type.definition.source.Callback(
                            semantic.enclosingModular.structureViewTreeElement,
                            semantic
                        )
                    }
                    ?.presentation?.let { itemPresentation ->
                        lookupElementPresentation.putItemPresentation(itemPresentation)
                    }
            }
    }
}
