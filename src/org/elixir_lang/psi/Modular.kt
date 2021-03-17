package org.elixir_lang.psi

import com.intellij.psi.ResolveState
import org.elixir_lang.ArityRange
import org.elixir_lang.Name
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.macroChildCallSequence
import org.elixir_lang.psi.impl.call.macroChildCalls

data class AccumulatorContinue<out R>(val accumulator: R, val `continue`: Boolean)

object Modular {
    @JvmStatic
    fun callDefinitionClauseCallSequence(modular: Call): Sequence<Call> =
            modular.macroChildCallSequence().filter { CallDefinitionClause.`is`(it) }

    @JvmStatic
    fun callDefinitionClauseCallWhile(modular: Call, resolveState: ResolveState, function: (Call, ResolveState) -> Boolean): Boolean {
        val childCalls = modular.macroChildCalls()
        var keepProcessing = true

        for (childCall in childCalls) {
            if (!resolveState.hasBeenVisited(childCall) && CallDefinitionClause.`is`(childCall)) {
                val childResolveState = resolveState.putVisitedElement(childCall)

                if (!function(childCall, childResolveState)) {
                    keepProcessing = false

                    break
                }
            }
        }

        return keepProcessing
    }

    @JvmStatic
    inline fun <R> callDefinitionClauseCallFoldWhile(
            modular: Call,
            initial: R,
            foldWhile: (Call, R) -> AccumulatorContinue<R>
    ): AccumulatorContinue<R> {
        val childCalls = modular.macroChildCalls()
        var accumulatorContinue = AccumulatorContinue(initial, true)

        for (childChild in childCalls) {
            if (CallDefinitionClause.`is`(childChild)) {
                accumulatorContinue = foldWhile(childChild, accumulatorContinue.accumulator)

                if (!accumulatorContinue.`continue`) {
                    break
                }
            }
        }

        return accumulatorContinue
    }

    inline fun <R> callDefinitionClauseCallFoldWhile(
            modular: Call,
            functionName: Name,
            initial: R,
            foldWhile: (Call, Name, ArityRange,  R) -> AccumulatorContinue<R>
    ): AccumulatorContinue<R> =
            callDefinitionClauseCallFoldWhile(modular, initial) { callDefinitionClauseCall, acc ->
                CallDefinitionClause.nameArityRange(callDefinitionClauseCall)?.let { (name, arityRange) ->
                    if (name == functionName) {
                            foldWhile(callDefinitionClauseCall, name, arityRange, acc)
                    } else {
                        null
                    }
                } ?:
                AccumulatorContinue(acc, true)
            }
}
