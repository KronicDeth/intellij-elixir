package org.elixir_lang.psi.impl

import com.intellij.psi.tree.IElementType
import org.elixir_lang.psi.*
import org.elixir_lang.psi.impl.WholeNumberImpl.BINARY_BASE
import org.elixir_lang.psi.impl.WholeNumberImpl.DECIMAL_BASE
import org.elixir_lang.psi.impl.WholeNumberImpl.HEXADECIMAL_BASE
import org.elixir_lang.psi.impl.WholeNumberImpl.OCTAL_BASE
import org.elixir_lang.psi.impl.WholeNumberImpl.UNKNOWN_BASE
import org.jetbrains.annotations.Contract

object DigitsImpl {
    @Contract(pure = true)
    @JvmStatic
    fun base(@Suppress("UNUSED_PARAMETER") binaryDigits: ElixirBinaryDigits): Int = BINARY_BASE

    @Contract(pure = true)
    @JvmStatic
    fun base(@Suppress("UNUSED_PARAMETER") decimalDigits: ElixirDecimalDigits): Int = DECIMAL_BASE

    @Contract(pure = true)
    @JvmStatic
    fun base(@Suppress("UNUSED_PARAMETER") hexadecimalDigits: ElixirHexadecimalDigits): Int = HEXADECIMAL_BASE

    @Contract(pure = true)
    @JvmStatic
    fun base(@Suppress("UNUSED_PARAMETER") octalDigits: ElixirOctalDigits): Int = OCTAL_BASE

    @Contract(pure = true)
    @JvmStatic
    fun base(@Suppress("UNUSED_PARAMETER") unknownBaseDigits: ElixirUnknownBaseDigits): Int = UNKNOWN_BASE

    @JvmStatic
    fun inBase(digits: Digits): Boolean = digits.node.firstChildNode.elementType === digits.validElementType()

    @JvmStatic
    fun validElementType(@Suppress("UNUSED_PARAMETER") binaryDigits: ElixirBinaryDigits): IElementType {
        return ElixirTypes.VALID_BINARY_DIGITS
    }

    @Contract(pure = true)
    @JvmStatic
    fun validElementType(@Suppress("UNUSED_PARAMETER") decimalDigits: ElixirDecimalDigits): IElementType {
        return ElixirTypes.VALID_DECIMAL_DIGITS
    }

    @JvmStatic
    fun validElementType(@Suppress("UNUSED_PARAMETER") hexadecimalDigits: ElixirHexadecimalDigits): IElementType {
        return ElixirTypes.VALID_HEXADECIMAL_DIGITS
    }

    @JvmStatic
    fun validElementType(@Suppress("UNUSED_PARAMETER") octalDigits: ElixirOctalDigits): IElementType {
        return ElixirTypes.VALID_OCTAL_DIGITS
    }

    @JvmStatic
    fun validElementType(@Suppress("UNUSED_PARAMETER") unknownBaseDigits: ElixirUnknownBaseDigits): IElementType? {
        return null
    }
}
