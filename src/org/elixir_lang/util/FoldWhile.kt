package org.elixir_lang.util

fun <T, R> Array<out T>.foldWhile(
    initial: R,
    folder: (element: T, accumulator: R) -> AccumulatorContinue<R>
): AccumulatorContinue<R> =
    asIterable().foldWhile(initial, folder)

fun <T, R> Sequence<T>.foldWhile(
    initial: R,
    folder: (element: T, accumulator: R) -> AccumulatorContinue<R>
): AccumulatorContinue<R> {
    var accumulatorContinue = AccumulatorContinue(initial, true)

    for (element in this) {
        accumulatorContinue = folder(element, accumulatorContinue.accumulator)

        if (!accumulatorContinue.`continue`) {
            break
        }
    }

    return accumulatorContinue
}

fun <T, R> Iterable<T>.foldWhile(
    initial: R,
    folder: (element: T, accumulator: R) -> AccumulatorContinue<R>
): AccumulatorContinue<R> {
    var accumulatorContinue = AccumulatorContinue(initial, true)

    for (element in this) {
        accumulatorContinue = folder(element, accumulatorContinue.accumulator)

        if (!accumulatorContinue.`continue`) {
            break
        }
    }

    return accumulatorContinue
}
