package org.elixir_lang.psi.impl

import com.intellij.lang.ASTNode
import org.elixir_lang.psi.Operator
import org.jetbrains.annotations.Contract


@Contract(pure = true)
fun Operator.operatorTokenNode(): ASTNode {
    return node
            .getChildren(
                    operatorTokenSet()
            )[0]
}
