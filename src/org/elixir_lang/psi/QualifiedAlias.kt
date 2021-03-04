package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.impl.stripAccessExpression

/**
 * <expression> dotInfixOperator alias
 */
interface QualifiedAlias : QualifiableAlias, Quotable {
    fun getAlias(): ElixirAlias
}

fun QualifiedAlias.qualifier(): PsiElement? {
    val children = this.children
    val operatorIndex = org.elixir_lang.psi.operation.Normalized.operatorIndex(children)

    // Strip the AccessExpression so it will be `QualifiableAlias` in the common case
    return org.elixir_lang.psi.operation.infix.Normalized.leftOperand(children, operatorIndex)?.stripAccessExpression()
}
