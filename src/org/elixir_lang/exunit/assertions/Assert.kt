package org.elixir_lang.exunit.assertions

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.exunit.Assertions
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArguments

object Assert {
    fun `is`(call: Call, state: ResolveState): Boolean =
            call.functionName()?.let { functionName ->
                functionName == "assert" &&
                        (call.resolvedFinalArity() in ARITY_RANGE) &&
                        Assertions.resolvesTo(call, state)
            } ?: false

    fun treeWalkUp(call: Call, state: ResolveState, keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
            call.finalArguments()?.let { arguments ->
                when (call.resolvedFinalArity()) {
                    // `assert(expression)` or `|> assert()`
                    1 -> when (arguments.size) {
                        // `assert(expression)`
                        1 -> keepProcessing(arguments[0], state)
                        else -> null
                    }
                    // `assert(expression, message)` or `|> assert(message)
                    2 -> when (arguments.size) {
                        // `assert(expression, message)`
                        2 -> keepProcessing(arguments[0], state)
                        else -> null
                    }
                    else -> {
                        Logger.error(logger, "assert arity outside of range (${ARITY_RANGE})", call)

                        null
                    }
                }
            } ?: true

    private val ARITY_RANGE = 1..2
    private val logger by lazy { com.intellij.openapi.diagnostic.Logger.getInstance(Assert::class.java) }
}
