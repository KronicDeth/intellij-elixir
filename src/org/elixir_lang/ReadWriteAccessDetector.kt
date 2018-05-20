package org.elixir_lang

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import org.elixir_lang.psi.AtNonNumericOperation
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.call.Call
import org.elixir_lang.reference.Callable

class ReadWriteAccessDetector : com.intellij.codeInsight.highlighting.ReadWriteAccessDetector() {
    override fun getExpressionAccess(expression: PsiElement): Access = Companion.getExpressionAccess(expression)

    override fun getReferenceAccess(referencedElement: PsiElement, reference: PsiReference): Access =
            if (referencedElement.isEquivalentTo(reference.element)) {
                Access.Write
            } else {
                Access.Read
            }

    override fun isDeclarationWriteAccess(element: PsiElement): Boolean = Companion.isDeclarationWriteAccess(element)

    override fun isReadWriteAccessible(element: PsiElement): Boolean =
        when (element) {
            is AtNonNumericOperation -> true
            is AtUnqualifiedNoParenthesesCall<*> -> true
            is Call -> isReadWriteAccessible(element)
            else -> false
        }

    private fun isReadWriteAccessible(call: Call): Boolean =
            Callable.isIgnored(call) ||
                    Callable.isParameter(call) ||
                    Callable.isParameterWithDefault(call) ||
                    Callable.isVariable(call)

    companion object {
        fun getExpressionAccess(expression: PsiElement): Access =
                if (expression !is AtNonNumericOperation && isDeclarationWriteAccess(expression)) {
                    Access.Write
                } else {
                    Access.Read
                }

        fun isDeclarationWriteAccess(element: PsiElement): Boolean =
                element.reference?.resolve()?.let { resolved -> resolved == element }
                        ?: false
    }
}
