package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.FOR
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.call.whileInStabBodyChildExpressions

object For {
    /**
     * Whether `call` is a `for ... <- ... do` call.
     */
    fun `is`(call: Call): Boolean = call.isCalling(KERNEL, FOR, 2)

    fun treeWalkDown(call: Call, resolveState: ResolveState, function: (PsiElement, ResolveState) -> Boolean): Boolean =
            call.whileInStabBodyChildExpressions { expression ->
                function(expression, resolveState)
            }
}
