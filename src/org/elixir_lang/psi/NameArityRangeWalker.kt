package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import org.elixir_lang.Arity
import org.elixir_lang.ArityRange
import org.elixir_lang.NameArityRange
import org.elixir_lang.ecto.Query
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArguments

open class NameArityRangeWalker(name: String, arityRange: ArityRange) {
    constructor(name: String, arity: Int): this(name, arity..arity)

    val nameArityRange = NameArityRange(name, arityRange)

    fun hasNameArity(call: Call) =
            call.functionName() == nameArityRange.name && hasArity(call)

    fun hasArity(call: Call) =
            call.resolvedFinalArity() in nameArityRange.arityRange

    fun walk(call: Call, state: ResolveState, keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
            call.finalArguments()?.let { arguments ->
                val resolvedFinalArity = call.resolvedFinalArity()

                if (resolvedFinalArity in nameArityRange.arityRange) {
                    walk(call, arguments, resolvedFinalArity, state, keepProcessing)
                } else {
                    Logger.error(logger, "${nameArityRange.name} arity outside of range (${nameArityRange.arityRange})", call)

                    true
                }
            } ?: true

    protected open fun walk(call: Call,
                            arguments: Array<PsiElement>,
                            resolvedFinalArity: Arity, state: ResolveState,
                            keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean = true

    companion object {
        @JvmStatic
        protected val logger by lazy { com.intellij.openapi.diagnostic.Logger.getInstance(NameArityRangeWalker::class.java) }
    }
}
