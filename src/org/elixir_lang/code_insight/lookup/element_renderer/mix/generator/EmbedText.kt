package org.elixir_lang.code_insight.lookup.element_renderer.mix.generator

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.codeInsight.lookup.LookupElementRenderer
import org.elixir_lang.NameArity
import org.elixir_lang.Visibility
import org.elixir_lang.code_insight.lookup.element_renderer.putItemPresentation
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.Exception.EXCEPTION
import org.elixir_lang.psi.Exception.MESSAGE

class EmbedText(val name: String) : LookupElementRenderer<LookupElement>() {
    override fun renderElement(element: LookupElement, lookupElementPresentation: LookupElementPresentation) {
        lookupElementPresentation.itemText = name
        lookupElementPresentation.appendTailText("()", true)
    }
}
