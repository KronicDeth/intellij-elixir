package org.elixir_lang.code_insight.lookup.element_renderer.exception

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.codeInsight.lookup.LookupElementRenderer
import org.elixir_lang.NameArity
import org.elixir_lang.semantic.Exception.Companion.EXCEPTION
import org.elixir_lang.semantic.Exception.Companion.MESSAGE

class CallDefinitionClause(val nameArity: NameArity) : LookupElementRenderer<LookupElement>() {
    override fun renderElement(element: LookupElement, lookupElementPresentation: LookupElementPresentation) {
        lookupElementPresentation.itemText = nameArity.name

        when {
            EXCEPTION.contains(nameArity) -> lookupElementPresentation.appendTailText("(message)", true)
            MESSAGE.contains(nameArity) -> lookupElementPresentation.appendTailText("(exception)", true)
            else -> Unit
        }
    }
}
