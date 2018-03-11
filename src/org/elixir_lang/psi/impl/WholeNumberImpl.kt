package org.elixir_lang.psi.impl

import org.elixir_lang.psi.*
import org.jetbrains.annotations.Contract
import java.util.*

object WholeNumberImpl {
    private const val BINARY_BASE = 2
    private const val DECIMAL_BASE = 10
    private const val HEXADECIMAL_BASE = 16
    private const val OCTAL_BASE = 8
    // NOTE: Unknown is all bases not 2, 8, 10, or 16, but 36 is used because all digits and letters are parsed.
    private const val UNKNOWN_BASE = 36

    // WholeNumber Functions

    @Contract(pure = true)
    @JvmStatic
    fun base(@Suppress("UNUSED_PARAMETER") binaryDigits: ElixirBinaryDigits): Int = BINARY_BASE

    @Contract(pure = true)
    @JvmStatic
    fun base(@Suppress("UNUSED_PARAMETER") binaryWholeNumber: ElixirBinaryWholeNumber): Int = BINARY_BASE

    @Contract(pure = true)
    @JvmStatic
    fun base(@Suppress("UNUSED_PARAMETER") decimalDigits: ElixirDecimalDigits): Int = DECIMAL_BASE

    @Contract(pure = true)
    @JvmStatic
    fun base(@Suppress("UNUSED_PARAMETER") decimalWholeNumber: ElixirDecimalWholeNumber): Int = DECIMAL_BASE

    @Contract(pure = true)
    @JvmStatic
    fun base(@Suppress("UNUSED_PARAMETER") hexadecimalDigits: ElixirHexadecimalDigits): Int = HEXADECIMAL_BASE

    @Contract(pure = true)
    @JvmStatic
    fun base(@Suppress("UNUSED_PARAMETER") hexadecimalWholeNumber: ElixirHexadecimalWholeNumber): Int =
            HEXADECIMAL_BASE

    @Contract(pure = true)
    @JvmStatic
    fun base(@Suppress("UNUSED_PARAMETER") octalDigits: ElixirOctalDigits): Int = OCTAL_BASE

    @Contract(pure = true)
    @JvmStatic
    fun base(@Suppress("UNUSED_PARAMETER") octalWholeNumber: ElixirOctalWholeNumber): Int = OCTAL_BASE

    @Contract(pure = true)
    @JvmStatic
    fun base(@Suppress("UNUSED_PARAMETER") unknownBaseDigits: ElixirUnknownBaseDigits): Int = UNKNOWN_BASE

    @Contract(pure = true)
    @JvmStatic
    fun base(@Suppress("UNUSED_PARAMETER") unknownBaseWholeNumber: ElixirUnknownBaseWholeNumber): Int =
            UNKNOWN_BASE

    @JvmStatic
    fun digitsList(binaryWholeNumber: ElixirBinaryWholeNumber): List<Digits> {
        val digitsList = LinkedList<Digits>()

        digitsList.addAll(binaryWholeNumber.binaryDigitsList)

        return digitsList
    }

    @JvmStatic
    fun digitsList(decimalWholeNumber: ElixirDecimalWholeNumber): List<Digits> {
        val digitsList = LinkedList<Digits>()

        digitsList.addAll(decimalWholeNumber.decimalDigitsList)

        return digitsList
    }

    @JvmStatic
    fun digitsList(hexadecimalWholeNumber: ElixirHexadecimalWholeNumber): List<Digits> {
        val digitsList = LinkedList<Digits>()

        digitsList.addAll(hexadecimalWholeNumber.hexadecimalDigitsList)

        return digitsList
    }

    @JvmStatic
    fun digitsList(octalWholeNumber: ElixirOctalWholeNumber): List<Digits> {
        val digitsList = LinkedList<Digits>()

        digitsList.addAll(octalWholeNumber.octalDigitsList)

        return digitsList
    }

    @JvmStatic
    fun digitsList(unknownBaseWholeNumber: ElixirUnknownBaseWholeNumber): List<Digits> {
        val digitsList = LinkedList<Digits>()

        digitsList.addAll(unknownBaseWholeNumber.unknownBaseDigitsList)

        return digitsList
    }
}
