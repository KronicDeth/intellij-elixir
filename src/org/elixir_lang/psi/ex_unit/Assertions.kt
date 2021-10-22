package org.elixir_lang.psi.ex_unit

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import org.elixir_lang.NameArityRange
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.resolvesToModularName

object Assertions {
    fun isDeclaringMacro(call: Call, state: ResolveState): Boolean =
            call.functionName()?.let { functionName ->
                DECLARING_MACRO_ARITY_RANGES_BY_NAME[functionName]?.let { arityRange ->
                    call.resolvedFinalArity() in arityRange && resolvesTo(call, state)
                }
            } ?: false

    private fun resolvesTo(call: Call, state: ResolveState): Boolean =
            resolvesToModularName(call, state, "ExUnit.Assertions")

    fun treeWalkUp(call: Call, state: ResolveState, keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
            call.functionName()?.let { functionName ->
                when (functionName) {
                    ASSERT_NAME_ARITY_RANGE.name ->
                        if (call.resolvedFinalArity() in ASSERT_NAME_ARITY_RANGE.arityRange) {
                            executeOnAssert(call, state, keepProcessing)
                        } else {
                            true
                        }
                    ASSERT_RECEIVE_NAME_ARITY_RANGE.name ->
                        if (call.resolvedFinalArity() in ASSERT_RECEIVE_NAME_ARITY_RANGE.arityRange) {
                            executeOnAssertReceive(call, state, keepProcessing)
                        } else {
                            true
                        }
                    ASSERT_RECEIVED_NAME_ARITY_RANGE.name ->
                        if (call.resolvedFinalArity() in ASSERT_RECEIVED_NAME_ARITY_RANGE.arityRange) {
                            executeOnAssertReceived(call, state, keepProcessing)
                        } else {
                            true
                        }
                    else -> true
                }
            } ?: true

    private fun executeOnAssert(call: Call,
                                state: ResolveState,
                                keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
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
                        Logger.error(logger, "assert arity outside of range (${ASSERT_NAME_ARITY_RANGE.arityRange})", call)

                        null
                    }
                }
            } ?: true

    private fun executeOnAssertReceive(call: Call,
                                       state: ResolveState,
                                       keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
            call.finalArguments()?.let { arguments ->
                // `assert_receive(pattern)` or `assert_receive(pattern, timeout)` or `assert_receive(pattern, timeout, failure_message)`
                if (call.resolvedFinalArity() == arguments.size) {
                    keepProcessing(arguments[0], state)
                } else {
                    null
                }
            } ?: true

    private fun executeOnAssertReceived(
            call: Call,
            state: ResolveState,
            keepProcessing: (element: PsiElement, state: ResolveState
            ) -> Boolean): Boolean =
            call.finalArguments()?.let { arguments ->
                // `assert_received(pattern)` or `assert_receive(pattern, failure_message)`
                if (call.resolvedFinalArity() == arguments.size) {
                    keepProcessing(arguments[0], state)
                } else {
                    null
                }
            } ?: true

    private val ASSERT_NAME_ARITY_RANGE = NameArityRange("assert", 1..2)
    private val ASSERT_RECEIVE_NAME_ARITY_RANGE = NameArityRange("assert_receive", 1..3)
    private val ASSERT_RECEIVED_NAME_ARITY_RANGE = NameArityRange("assert_received", 1..2)
    private val DECLARING_MACRO_NAME_ARITY_RANGES = arrayOf(
            ASSERT_NAME_ARITY_RANGE,
            ASSERT_RECEIVE_NAME_ARITY_RANGE,
            ASSERT_RECEIVED_NAME_ARITY_RANGE
    )
    private val DECLARING_MACRO_ARITY_RANGES_BY_NAME =
            DECLARING_MACRO_NAME_ARITY_RANGES.map { it.name to it.arityRange }.toMap()

    private val logger by lazy { com.intellij.openapi.diagnostic.Logger.getInstance(Assertions::class.java) }
}
