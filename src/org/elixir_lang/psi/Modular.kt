package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.util.Function
import org.apache.commons.lang.math.IntRange
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.Named
import org.elixir_lang.psi.impl.call.macroChildCallSequence
import org.elixir_lang.psi.impl.call.macroChildCalls
import org.elixir_lang.structure_view.element.CallDefinitionClause

data class AccumulatorContinue<out R>(val accumulator: R, val `continue`: Boolean)

object Modular {
    @JvmStatic
    fun callDefinitionClauseCallSequence(modular: Call): Sequence<Call> =
            modular.macroChildCallSequence().filter { CallDefinitionClause.`is`(it) }

    @JvmStatic
    fun callDefinitionClauseCallWhile(modular: Call, function: (Call) -> Boolean): Boolean {
        val childCalls = modular.macroChildCalls()
        var keepProcessing = true

        for (childCall in childCalls) {
            if (CallDefinitionClause.`is`(childCall) && !function(childCall)) {
                keepProcessing = false

                break
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
            functionName: String,
            initial: R,
            foldWhile: (Call, String, IntRange,  R) -> AccumulatorContinue<R>
    ): AccumulatorContinue<R> =
            callDefinitionClauseCallFoldWhile(modular, initial) { callDefinitionClauseCall, acc ->
                CallDefinitionClause.nameArityRange(callDefinitionClauseCall)?.let { nameArityRange ->
                    nameArityRange.first?.let { name ->
                        if (name == functionName) {
                            nameArityRange.second?.let { arityRange ->
                                foldWhile(callDefinitionClauseCall, name, arityRange, acc)
                            }
                        } else {
                            null
                        }
                    }
                } ?:
                AccumulatorContinue(acc, true)
            }

    inline fun <R> callDefinitionClauseCallFoldWhile(
            modular: Call,
            functionName: String,
            resolvedFinalArity: Int,
            initial: R,
            foldWhile: (Call, String, IntRange, R) -> AccumulatorContinue<R>
    ): AccumulatorContinue<R> =
            callDefinitionClauseCallFoldWhile(
                    modular,
                    functionName,
                    initial
            ) { callDefinitionClauseCall, name, arityRange, acc ->
                if (arityRange.containsInteger(resolvedFinalArity)) {
                    foldWhile(callDefinitionClauseCall, name, arityRange, acc)
                } else {
                    AccumulatorContinue(acc, true)
                }
            }

    @JvmStatic
    fun forEachCallDefinitionClauseNameIdentifier(
            modular: Call,
            functionName: String?,
            resolvedFinalArity: Int,
            function: Function<PsiElement, Boolean>
    ) {
        callDefinitionClauseCallWhile(modular, functionName, resolvedFinalArity) { call ->
            var keepProcessing = true

            if (call is Named) {
                val nameIdentifier = call.nameIdentifier

                if (nameIdentifier != null && !function.`fun`(nameIdentifier)) {
                    keepProcessing = false
                }
            }

            keepProcessing
        }
    }

    private fun callDefinitionClauseCallWhile(modular: Call,
                                              functionName: String?,
                                              resolvedFinalArity: Int,
                                              function: (Call) -> Boolean) {
        if (functionName != null) {
            callDefinitionClauseCallWhile(modular) { callDefinitionClauseCall ->
                val nameArityRange = CallDefinitionClause.nameArityRange(callDefinitionClauseCall)
                var keepProcessing = true

                if (nameArityRange != null) {
                    val name = nameArityRange.first

                    if (name != null && name == functionName) {
                        val arityRange = nameArityRange.second

                        if (arityRange.containsInteger(resolvedFinalArity) && !function(callDefinitionClauseCall)) {
                            keepProcessing = false
                        }
                    }
                }

                keepProcessing
            }
        }
    }
}
