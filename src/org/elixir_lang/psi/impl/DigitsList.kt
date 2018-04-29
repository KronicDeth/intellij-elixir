package org.elixir_lang.psi.impl

import org.elixir_lang.psi.Digits

fun List<Digits>.inBase(): Boolean {
    var validDigitsCount = 0
    var invalidDigitsCount = 0

    for (digits in this) {
        if (digits.inBase()) {
            validDigitsCount++
        } else {
            invalidDigitsCount++
        }
    }

    return invalidDigitsCount < 1 && validDigitsCount > 0
}

fun List<Digits>.textToString(): String = this.joinToString("") { it.text }

