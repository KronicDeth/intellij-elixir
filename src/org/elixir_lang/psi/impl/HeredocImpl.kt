package org.elixir_lang.psi.impl

import org.elixir_lang.psi.*

object HeredocImpl {
    @JvmStatic
    fun getHeredocLineList(interpolatedCharListHeredocLined: InterpolatedCharListHeredocLined): List<HeredocLine> =
            interpolatedCharListHeredocLined.interpolatedCharListHeredocLineList

    @JvmStatic
    fun getHeredocLineList(interpolatedStringHeredocLined: InterpolatedStringHeredocLined): List<HeredocLine> =
            interpolatedStringHeredocLined.interpolatedStringHeredocLineList

    @JvmStatic
    fun getHeredocLineList(charListHeredoc: ElixirCharListHeredoc): List<HeredocLine> =
            charListHeredoc.charListHeredocLineList

    @JvmStatic
    fun getHeredocLineList(interpolatedRegexHeredoc: ElixirInterpolatedRegexHeredoc): List<HeredocLine> =
            interpolatedRegexHeredoc.interpolatedRegexHeredocLineList

    @JvmStatic
    fun getHeredocLineList(interpolatedSigilHeredoc: ElixirInterpolatedSigilHeredoc): List<HeredocLine> =
            interpolatedSigilHeredoc.interpolatedSigilHeredocLineList

    @JvmStatic
    fun getHeredocLineList(interpolatedWordsHeredoc: ElixirInterpolatedWordsHeredoc): List<HeredocLine> =
            interpolatedWordsHeredoc.interpolatedWordsHeredocLineList

    @JvmStatic
    fun getHeredocLineList(literalCharListSigilHeredoc: ElixirLiteralCharListSigilHeredoc): List<HeredocLine> =
            literalCharListSigilHeredoc.literalCharListHeredocLineList

    @JvmStatic
    fun getHeredocLineList(literalRegexSigilHeredoc: ElixirLiteralRegexHeredoc): List<HeredocLine> =
         literalRegexSigilHeredoc.literalRegexHeredocLineList

    @JvmStatic
    fun getHeredocLineList(literalSigilHeredoc: ElixirLiteralSigilHeredoc): List<HeredocLine> =
            literalSigilHeredoc.literalSigilHeredocLineList

    @JvmStatic
    fun getHeredocLineList(literalStringSigilHeredoc: ElixirLiteralStringSigilHeredoc): List<HeredocLine> =
            literalStringSigilHeredoc.literalStringHeredocLineList

    @JvmStatic
    fun getHeredocLineList(literalWordsSigilHeredoc: ElixirLiteralWordsHeredoc): List<HeredocLine> =
            literalWordsSigilHeredoc.literalWordsHeredocLineList

    @JvmStatic
    fun getHeredocLineList(stringHeredoc: ElixirStringHeredoc): List<HeredocLine> =
            stringHeredoc.stringHeredocLineList
}
