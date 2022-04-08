package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.hasKeywordKey
import org.elixir_lang.psi.operation.Match
import org.elixir_lang.semantic.semantic

object Variable {
    @JvmStatic
    fun isDeclaration(element: PsiElement): Boolean =
        element is UnqualifiedNoArgumentsCall<*> && element.resolvedFinalArity() == 0 &&
                (element.parent is Match || isOnlyChildOfQuote(element))

    @JvmStatic
    fun isOnlyChildOfQuote(element: PsiElement): Boolean =
        when (element) {
            is UnqualifiedNoArgumentsCall<*>,
            is QuotableKeywordList,
            is ElixirParenthesesArguments,
            is ElixirMatchedParenthesesArguments ->
                isOnlyChildOfQuote(element.parent)
            is QuotableKeywordPair -> if (element.hasKeywordKey("do")) {
                isOnlyChildOfQuote(element.parent)
            } else {
                false
            }
            is Call -> element.semantic is org.elixir_lang.semantic.quote.Call
            else -> false
        }
}
