package org.elixir_lang.psi.impl

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.Arguments
import org.elixir_lang.psi.ElixirNoParenthesesOneArgument
import org.elixir_lang.psi.ElixirNoParenthesesStrict
import org.elixir_lang.psi.ElixirParenthesesArguments
import org.jetbrains.annotations.Contract

object ArgumentsImpl {
    @Contract(pure = true)
    @JvmStatic
    fun arguments(noParenthesesOneArgument: ElixirNoParenthesesOneArgument): Array<PsiElement> {
        val children = noParenthesesOneArgument.children

        return if (children.size == 1) {
            val child = children[0]

            if (child is Arguments) {
                child.arguments()
            } else {
                children
            }
        } else {
            children
        }
    }

    @Contract(pure = true)
    @JvmStatic
    fun arguments(noParenthesesStrict: ElixirNoParenthesesStrict): Array<PsiElement> = noParenthesesStrict.children

    @Contract(pure = true)
    @JvmStatic
    fun arguments(parenthesesArguments: ElixirParenthesesArguments): Array<PsiElement> = parenthesesArguments.children
}
