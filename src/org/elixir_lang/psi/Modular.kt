package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import org.elixir_lang.Name
import org.elixir_lang.beam.psi.impl.ModuleImpl
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.macroChildCallSequence
import org.elixir_lang.psi.impl.call.macroChildCalls
import org.elixir_lang.psi.impl.childExpressions

data class AccumulatorContinue<out R>(val accumulator: R, val `continue`: Boolean) {
    companion object {
        fun <R> childExpressionsFoldWhile(
            parent: PsiElement,
            forward: Boolean,
            initial: R,
            folder: (element: PsiElement, accumulator: R) -> AccumulatorContinue<R>
        ): AccumulatorContinue<R> =
            parent.childExpressions(forward).let { foldWhile(it, initial, folder) }

        fun <T, R> foldWhile(
            array: Array<out T>,
            initial: R,
            folder: (element: T, accumulator: R) -> AccumulatorContinue<R>
        ): AccumulatorContinue<R> =
            foldWhile(array.asIterable(), initial, folder)

        fun <T, R> foldWhile(
            sequence: Sequence<T>,
            initial: R,
            folder: (element: T, accumulator: R) -> AccumulatorContinue<R>
        ): AccumulatorContinue<R> {
            var accumulatorContinue = AccumulatorContinue(initial, true)

            for (element in sequence) {
                accumulatorContinue = folder(element, accumulatorContinue.accumulator)

                if (!accumulatorContinue.`continue`) {
                    break
                }
            }

            return accumulatorContinue
        }

        fun <T, R> foldWhile(
            iterable: Iterable<T>,
            initial: R,
            folder: (element: T, accumulator: R) -> AccumulatorContinue<R>
        ): AccumulatorContinue<R> {
            var accumulatorContinue = AccumulatorContinue(initial, true)

            for (element in iterable) {
                accumulatorContinue = folder(element, accumulatorContinue.accumulator)

                if (!accumulatorContinue.`continue`) {
                    break
                }
            }

            return accumulatorContinue
        }
    }
}

object Modular {
    @JvmStatic
    fun callDefinitionClauseCallSequence(modular: Call): Sequence<Call> =
        modular.macroChildCallSequence().filter { CallDefinitionClause.`is`(it) }

    fun callDefinitionClauseCallWhile(
        modular: PsiElement,
        resolveState: ResolveState,
        function: (Call, ResolveState) -> Boolean
    ): Boolean =
        when (modular) {
            is Call -> callDefinitionClauseCallWhile(modular, resolveState, function)
            is ModuleImpl<*> -> callDefinitionClauseCallWhile(modular, resolveState, function)
            else -> true
        }

    fun callDefinitionClauseCallWhile(
        modular: Call,
        resolveState: ResolveState,
        function: (Call, ResolveState) -> Boolean
    ): Boolean {
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
        modular: PsiElement,
        functionName: Name,
        initial: R,
        foldWhile: (PsiElement, Name, ArityInterval, R) -> AccumulatorContinue<R>
    ): AccumulatorContinue<R> =
        when (modular) {
            is Call -> callDefinitionClauseCallFoldWhile(modular, functionName, initial, foldWhile)
            is ModuleImpl<*> -> callDefinitionClauseCallFoldWhile(modular, functionName, initial, foldWhile)
            else -> AccumulatorContinue(initial, true)
        }

    inline fun <R> callDefinitionClauseCallFoldWhile(
        modular: Call,
        functionName: Name,
        initial: R,
        foldWhile: (PsiElement, Name, ArityInterval, R) -> AccumulatorContinue<R>
    ): AccumulatorContinue<R> =
        callDefinitionClauseCallFoldWhile(modular, initial) { callDefinitionClauseCall, acc ->
            CallDefinitionClause
                .nameArityInterval(callDefinitionClauseCall, ResolveState.initial())
                ?.let { (name, arityInterval) ->
                    if (name == functionName) {
                        foldWhile(callDefinitionClauseCall, name, arityInterval, acc)
                    } else {
                        null
                    }
                }
                ?: AccumulatorContinue(acc, true)
        }

    inline fun <R> callDefinitionClauseCallFoldWhile(
        modular: ModuleImpl<*>,
        functionName: Name,
        initial: R,
        foldWhile: (PsiElement, Name, ArityInterval, R) -> AccumulatorContinue<R>
    ): AccumulatorContinue<R> =
        modular
            .callDefinitions()
            .fold(AccumulatorContinue(initial, true)) { acc, callDefinitionImpl ->
                val nameArityInterval = callDefinitionImpl.nameArityInterval
                val name = nameArityInterval.name

                if (name == functionName) {
                    foldWhile(callDefinitionImpl, name, nameArityInterval.arityInterval, acc.accumulator)
                } else {
                    acc
                }
            }
}
