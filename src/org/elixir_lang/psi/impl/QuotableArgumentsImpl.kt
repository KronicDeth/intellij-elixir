package org.elixir_lang.psi.impl

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.psi.*
import org.elixir_lang.psi.impl.QuotableImpl.NIL
import org.jetbrains.annotations.Contract

object QuotableArgumentsImpl {
    val DO = OtpErlangAtom("do")

    @Contract(pure = true)
    @JvmStatic
    fun quoteArguments(arguments: Arguments): Array<OtpErlangObject> =
            arguments
                    .arguments()
                    .map { it as Quotable }
                    .map(Quotable::quote)
                    .toTypedArray()

    @Contract(pure = true)
    @JvmStatic
    fun quoteArguments(blockList: ElixirBlockList): Array<OtpErlangObject> =
            blockList
                    .blockItemList
                    .map(ElixirBlockItem::quote)
                    .toTypedArray()

    @Contract(pure = true)
    @JvmStatic
    fun quoteArguments(
            unqualifiedNoParenthesesManyArgumentsCall: ElixirUnqualifiedNoParenthesesManyArgumentsCall
    ): Array<OtpErlangObject> =
            unqualifiedNoParenthesesManyArgumentsCall
                    .primaryArguments()
                    .map { it as Quotable }
                    .map(Quotable::quote)
                    .toTypedArray()

    @Contract(pure = true)
    @JvmStatic
    fun quoteArguments(doBlock: ElixirDoBlock): Array<OtpErlangObject> {
        val doValue = doBlock.stab?.quote() ?: NIL

        val quotedKeywordPairListPrefix = arrayOf(DO, doValue).let(::OtpErlangTuple).let { listOf(it) }
        val quotedKeywordPairListSuffix = doBlock.blockList?.quoteArguments()?.toList() ?: emptyList()
        val quotedKeywordPairList = quotedKeywordPairListPrefix + quotedKeywordPairListSuffix

        return quotedKeywordPairList.toTypedArray().let(::OtpErlangList).let { arrayOf(it) }
    }

    @Contract(pure = true)
    @JvmStatic
    fun quoteArguments(mapConstructionArguments: ElixirMapConstructionArguments): Array<OtpErlangObject> =
            mapConstructionArguments
                    .arguments()
                    .map { it as Quotable }
                    .flatMap {
                        it
                                .quote()
                                .let { it as OtpErlangList }
                                .elements()
                                .asList()
                    }.toTypedArray()

    @Contract(pure = true)
    @JvmStatic
    fun quoteArguments(noParenthesesArguments: ElixirNoParenthesesArguments): Array<OtpErlangObject> =
            noParenthesesArguments
                    .children
                    .single()
                    .let { it as QuotableArguments }
                    .quoteArguments()

    @Contract(pure = true)
    @JvmStatic
    fun quoteArguments(parenthesesArguments: ElixirParenthesesArguments): Array<OtpErlangObject> =
            parenthesesArguments
                    .arguments()
                    .map { it as Quotable }
                    .map(Quotable::quote)
                    .toTypedArray()
}
