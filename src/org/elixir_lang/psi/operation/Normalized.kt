package org.elixir_lang.psi.operation

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.Operator
import org.jetbrains.annotations.Contract

/**
 * Canonical children of an [Operation], which converts any errors to `null`.
 */
object Normalized {
    @Contract(pure = true)
    @JvmStatic
    fun operator(operation: Operation): Operator {
        val children = operation.children
        val operatorIndex = operatorIndex(children)

        return operator(children, operatorIndex)
    }

    @Contract(pure = true)
    @JvmStatic
    fun operator(children: Array<PsiElement>, operatorIndex: Int): Operator {
        return children[operatorIndex] as Operator
    }

    @Contract(pure = true)
    @JvmStatic
    fun operatorIndex(children: Array<PsiElement>): Int {
        var operatorIndex = -1
        for (i in children.indices) {
            if (children[i] is Operator) {
                operatorIndex = i
                break
            }
        }

        assert(operatorIndex != -1)

        return operatorIndex
    }
}
