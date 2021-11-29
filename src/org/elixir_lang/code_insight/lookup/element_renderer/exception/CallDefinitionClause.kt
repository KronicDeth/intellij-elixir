package org.elixir_lang.code_insight.lookup.element_renderer.exception

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.codeInsight.lookup.LookupElementRenderer
import org.elixir_lang.NameArity
import org.elixir_lang.Visibility
import org.elixir_lang.code_insight.lookup.element_renderer.putItemPresentation
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.Exception.EXCEPTION
import org.elixir_lang.psi.Exception.MESSAGE

class CallDefinitionClause(val nameArity: NameArity) : LookupElementRenderer<LookupElement>() {
    override fun renderElement(element: LookupElement, lookupElementPresentation: LookupElementPresentation) {
        lookupElementPresentation.itemText = nameArity.name

        when (nameArity) {
            EXCEPTION -> lookupElementPresentation.appendTailText("(message)", true)
            MESSAGE -> lookupElementPresentation.appendTailText("(exception)", true)
            else -> Unit
        }
    }
}
