package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.impl.childExpressions

data class AccumulatorContinue<out R>(val accumulator: R, val `continue`: Boolean) {
    companion object {
        fun <R> childExpressionsFoldWhile(
                parent: PsiElement,
                forward: Boolean,
                initial: R,
                folder: (element: PsiElement, accumulator: R) -> AccumulatorContinue<R>): AccumulatorContinue<R> =
            parent.childExpressions(forward).let { foldWhile(it, initial, folder) }

        fun <T, R>foldWhile(array: Array<out T>, initial: R, folder: (element: T, accumulator: R) -> AccumulatorContinue<R>): AccumulatorContinue<R> =
                foldWhile(array.asIterable(), initial, folder)

        fun <T, R>foldWhile(sequence: Sequence<T>, initial: R, folder: (element: T, accumulator: R) -> AccumulatorContinue<R>): AccumulatorContinue<R> {
            var accumulatorContinue = AccumulatorContinue(initial, true)

            for (element in sequence) {
                accumulatorContinue = folder(element, accumulatorContinue.accumulator)

                if (!accumulatorContinue.`continue`) {
                    break
                }
            }

            return accumulatorContinue
        }

        fun <T, R>foldWhile(iterable: Iterable<T>, initial: R, folder: (element: T, accumulator: R) -> AccumulatorContinue<R>): AccumulatorContinue<R> {
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

