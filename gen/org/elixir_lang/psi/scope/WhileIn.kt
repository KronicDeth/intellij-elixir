package org.elixir_lang.psi.scope

import com.intellij.psi.PsiElement

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

    inline fun <T> whileIn(list: List<T>, keepProcessing: (element: T) -> Boolean): Boolean {
        var accumlatedKeepProcessing = true

        for (element in list) {
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
