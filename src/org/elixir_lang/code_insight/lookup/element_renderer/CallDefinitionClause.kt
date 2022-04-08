package org.elixir_lang.code_insight.lookup.element_renderer

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.codeInsight.lookup.LookupElementRenderer
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import org.elixir_lang.semantic.semantic

class CallDefinitionClause(private val name: String) : LookupElementRenderer<LookupElement>() {
    override fun renderElement(element: LookupElement, presentation: LookupElementPresentation) {
        presentation.itemText = name
        presentation.isItemTextBold = true
        val psiElement = element.psiElement
        if (psiElement == null) {
            renderObject(element)
        } else {
            renderElement(psiElement, presentation)
        }
    }

    private fun renderElement(psiElement: PsiElement, presentation: LookupElementPresentation) {
        when (val semantic = psiElement.semantic) {
            is org.elixir_lang.semantic.call.definition.Clause -> renderElement(semantic.presentation, presentation)
            else -> Unit
        }
    }

    private fun renderElement(
        itemPresentation: ItemPresentation,
        lookupElementPresentation: LookupElementPresentation
    ) {
        lookupElementPresentation.icon = itemPresentation.getIcon(true)
        val presentableText = itemPresentation.presentableText
        if (presentableText != null) {
            val nameLength = name.length
            val presentableTextLength = presentableText.length
            if (nameLength <= presentableTextLength) {
                lookupElementPresentation.appendTailText(presentableText.substring(nameLength), true)
            }
        }
        val locationString = itemPresentation.locationString
        if (locationString != null) {
            lookupElementPresentation.appendTailText(" ($locationString)", false)
        }
    }

    private fun renderObject(lookupElement: LookupElement) {
        val logger = Logger.getInstance(CallDefinitionClause::class.java)
        val `object` = lookupElement.getObject()
        val title = "CallDefinitionClause render called on LookupElement with null getPsiElement"
        val message = """
            ## name
            
            ```
            $name
            ```
            
            ## getObject()
            
            ### toString()
            
            ```
            $`object`
            ```
            
            ### getClass().getName()
            
            ```
            ${`object`.javaClass.name}
            ```
            
            """.trimIndent()
        logger.error(message, Throwable(title))
    }
}
