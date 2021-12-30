package org.elixir_lang.psi.impl

import com.ericsson.otp.erlang.*
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.ElixirLine
import org.elixir_lang.psi.Parent
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.javaString
import org.elixir_lang.psi.impl.QuotableImpl.childNodes
import org.elixir_lang.psi.impl.QuotableImpl.metadata
import org.elixir_lang.psi.impl.QuotableImpl.quotedChildNodes
import org.elixir_lang.psi.impl.QuotableImpl.quotedFunctionCall
import org.jetbrains.annotations.Contract

private val UTF_8 = OtpErlangAtom("utf8")

@Contract(pure = true)
fun ElixirLine.quoteAsAtom(): OtpErlangObject {
    val quotedChildNodes = quotedChildNodes(AtomableParent(this), *childNodes(lineBody!!));

    return quotedToAtom(quotedChildNodes, this)
}

private fun quotedToAtom(quoted: OtpErlangObject, element: PsiElement): OtpErlangObject = when (quoted) {
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
            metadata(element),
            quoted,
            UTF_8
    )
}
