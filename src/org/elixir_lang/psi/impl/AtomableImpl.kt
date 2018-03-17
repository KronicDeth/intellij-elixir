package org.elixir_lang.psi.impl

import com.ericsson.otp.erlang.*
import org.elixir_lang.psi.ElixirCharListLine
import org.elixir_lang.psi.ElixirStringLine
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.javaString
import org.elixir_lang.psi.impl.QuotableImpl.metadata
import org.elixir_lang.psi.impl.QuotableImpl.quotedFunctionCall
import org.jetbrains.annotations.Contract

private val UTF_8 = OtpErlangAtom("utf8")

@Contract(pure = true)
fun ElixirCharListLine.quoteAsAtom(): OtpErlangObject {
    val quoted = quote()

    return if (quoted is OtpErlangString) {
        val atomText = quoted.stringValue()
        OtpErlangAtom(atomText)
    } else {
        val quotedStringToCharListCall = quoted as OtpErlangTuple
        val quotedStringToCharListArguments = quotedStringToCharListCall.elementAt(2) as OtpErlangList
        val binaryConstruction = quotedStringToCharListArguments.head

        quotedFunctionCall(
                "erlang",
                "binary_to_atom",
                quotedStringToCharListCall.elementAt(1) as OtpErlangList,
                binaryConstruction,
                UTF_8
        )
    }
}

@Contract(pure = true)
fun ElixirStringLine.quoteAsAtom(): OtpErlangObject {
    val quoted = quote()

    return when (quoted) {
        is OtpErlangBinary -> {
            val atomText = javaString(quoted)
            OtpErlangAtom(atomText)
        }
        is OtpErlangString -> {
            val atomText = quoted.stringValue()
            OtpErlangAtom(atomText)
        }
        else -> quotedFunctionCall(
                "erlang",
                "binary_to_atom",
                metadata(this),
                quoted,
                UTF_8
        )
    }
}
