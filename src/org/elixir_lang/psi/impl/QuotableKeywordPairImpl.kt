package org.elixir_lang.psi.impl

import com.ericsson.otp.erlang.OtpErlangAtom
import com.intellij.openapi.util.Computable
import org.elixir_lang.mix.project.computeReadAction
import org.elixir_lang.psi.ElixirKeywordPair
import org.elixir_lang.psi.ElixirNoParenthesesKeywordPair
import org.elixir_lang.psi.Quotable
import org.elixir_lang.psi.QuotableKeywordPair

fun QuotableKeywordPair.hasKeywordKey(keywordKeyText: String): Boolean {
    val keywordKey = keywordKey
    var has = false

    if (computeReadAction(Computable<String> { keywordKey.text }) == keywordKeyText) {
        has = true
    } else {
        val quotedKeywordKey = keywordKey.quote()

        if (quotedKeywordKey is OtpErlangAtom) {

            if (quotedKeywordKey.atomValue() == keywordKeyText) {
                has = true
            }
        }
    }

    return has
}

object QuotableKeywordPairImpl {
    @JvmStatic
    fun getKeywordValue(keywordPair: ElixirKeywordPair): Quotable {
        val children = keywordPair.children

        assert(children.size >= 2)

        return children[1].stripAccessExpression() as Quotable
    }

    @JvmStatic
    fun getKeywordValue(noParenthesesKeywordPair: ElixirNoParenthesesKeywordPair): Quotable {
        val children = noParenthesesKeywordPair.children

        assert(children.size >= 2)

        return children[1].stripAccessExpression() as Quotable
    }
}
