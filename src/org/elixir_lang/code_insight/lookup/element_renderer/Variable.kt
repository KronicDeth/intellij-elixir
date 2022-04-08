package org.elixir_lang.code_insight.lookup.element_renderer

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.codeInsight.lookup.LookupElementRenderer
import com.intellij.psi.PsiElement
import com.intellij.ui.JBColor
import org.elixir_lang.ElixirSyntaxHighlighter
import org.elixir_lang.Icons
import org.elixir_lang.annotator.Parameter
import org.elixir_lang.psi.operation.InMatch
import org.elixir_lang.psi.operation.Match
import org.elixir_lang.reference.Callable.Companion.isIgnored
import org.elixir_lang.reference.Callable.Companion.isParameterWithDefault
import org.elixir_lang.reference.Callable.Companion.isVariable
import org.jetbrains.annotations.Contract
import java.awt.Color
import javax.swing.Icon

class Variable(private val name: String) : LookupElementRenderer<LookupElement>() {
    /*
     * Public Instance Methods
     */
    override fun renderElement(element: LookupElement, presentation: LookupElementPresentation) {
        presentation.itemText = name
        presentation.isItemTextBold = true
        val psiElement = element.psiElement!!
        presentation.icon = icon(psiElement)
        presentation.itemTextForeground = color(psiElement)

        /* Add a space between variable name and match.
           See https://github.com/KronicDeth/intellij-elixir/issues/506 */
        presentation.appendTailText(" ", false)
        val psiElementTextRange = psiElement.textRange
        val enclosingMatch = enclosingMatch(psiElement)
        val enclosingMatchTextRange = enclosingMatch.textRange
        val enclosingMatchText = enclosingMatch.text
        val enclosingMatchTextRangeStartOffset = enclosingMatchTextRange.startOffset
        val itemStartOffset = psiElementTextRange.startOffset - enclosingMatchTextRangeStartOffset
        val prefix = enclosingMatchText.substring(0, itemStartOffset)
        presentation.appendTailText(prefix, true)
        val itemEndOffset = psiElementTextRange.endOffset - enclosingMatchTextRangeStartOffset
        val item = enclosingMatchText.substring(itemStartOffset, itemEndOffset)
        presentation.appendTailText(item, false)
        val suffix = enclosingMatchText.substring(
            itemEndOffset,
            enclosingMatchTextRange.endOffset - enclosingMatchTextRangeStartOffset
        )
        presentation.appendTailText(suffix, true)
    }

    /*
     * Private Instance Methods
     */
    @Contract(pure = true)
    private fun color(element: PsiElement): Color {
        var color = JBColor.foreground()
        if (isIgnored(element)) {
            color = ElixirSyntaxHighlighter.IGNORED_VARIABLE.defaultAttributes.foregroundColor
        } else {
            val parameter = Parameter(element)
            val parameterType = Parameter.putParameterized(parameter).type
            if (parameterType != null) {
                if (parameterType === Parameter.Type.VARIABLE) {
                    color = ElixirSyntaxHighlighter.PARAMETER.defaultAttributes.foregroundColor
                }
            } else if (isParameterWithDefault(element)) {
                color = ElixirSyntaxHighlighter.PARAMETER.defaultAttributes.foregroundColor
            } else if (isVariable(element)) {
                color = ElixirSyntaxHighlighter.VARIABLE.defaultAttributes.foregroundColor
            }
        }
        return color
    }

    @Contract(pure = true)
    private fun enclosingMatch(ancestor: PsiElement): PsiElement {
        var enclosingMatch = ancestor
        val parent = ancestor.parent
        if (parent is InMatch || parent is Match) {
            enclosingMatch = parent
        }
        return enclosingMatch
    }

    @Contract(pure = true)
    private fun icon(element: PsiElement): Icon? {
        val parameter = Parameter(element)
        val parameterType = Parameter.putParameterized(parameter).type
        return if (parameterType != null) {
            Icons.PARAMETER
        } else {
            Icons.VARIABLE
        }
    }
}
