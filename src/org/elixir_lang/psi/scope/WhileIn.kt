package org.elixir_lang.psi.scope

object WhileIn {
    inline fun <T> whileIn(array: Array<T>, keepProcessing: (element: T) -> Boolean): Boolean {
        var accumlatedKeepProcessing = true

        for (element in array) {
            accumlatedKeepProcessing = keepProcessing(element)

            if (!accumlatedKeepProcessing) {
                break
            }
        }

        return accumlatedKeepProcessing
    }

    inline fun <T> whileIn(collection: Collection<T>, keepProcessing: (element: T) -> Boolean): Boolean {
        var accumlatedKeepProcessing = true

        for (element in collection) {
            accumlatedKeepProcessing = keepProcessing(element)

            if (!accumlatedKeepProcessing) {
                break
            }
        }

        return accumlatedKeepProcessing
    }

    inline fun <T> whileIn(sequence: Sequence<T>, keepProcessing: (element: T) -> Boolean): Boolean {
        var accumlatedKeepProcessing = true

        for (element in sequence) {
            accumlatedKeepProcessing = keepProcessing(element)

            if (!accumlatedKeepProcessing) {
                break
            }
        }

        return accumlatedKeepProcessing
    }
}
