package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import org.elixir_lang.ArityRange
import org.elixir_lang.NameArityRange
import org.elixir_lang.psi.call.Call

open class NameArityRangeWalker(name: String, arityRange: ArityRange) {
    val nameArityRange = NameArityRange(name, arityRange)

    fun hasNameArity(call: Call) =
            call.functionName() == nameArityRange.name && hasArity(call)

    fun hasArity(call: Call) =
            call.resolvedFinalArity() in nameArityRange.arityRange

    open fun walk(call: Call, state: ResolveState, keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
            true
}
