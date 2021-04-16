package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.util.siblings
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.FOR
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.scope.WhileIn.whileIn

object For {
    /**
     * Whether `call` is a `for ... <- ... do` call.
     */
    fun `is`(call: Call): Boolean = call.isCalling(KERNEL, FOR, 2)

    fun treeWalkDown(call: Call, resolveState: ResolveState, function: (PsiElement, ResolveState) -> Boolean): Boolean =
            call
                    .doBlock
                    ?.stab
                    ?.stabBody
                    ?.firstChild
                    ?.siblings()
                    ?.filter { it.node is CompositeElement }
                    ?.let { expressionSequence ->
                        whileIn(expressionSequence) { expression ->
                            function(expression, resolveState)
                        }
                    }
                    ?: true
}
