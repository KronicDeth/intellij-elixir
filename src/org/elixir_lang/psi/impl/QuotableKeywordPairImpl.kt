package org.elixir_lang.psi.impl

import org.elixir_lang.psi.ElixirKeywordPair
import org.elixir_lang.psi.ElixirNoParenthesesKeywordPair
import org.elixir_lang.psi.Quotable

object QuotableKeywordPairImpl {
    @JvmStatic
    fun getKeywordValue(keywordPair: ElixirKeywordPair): Quotable {
        val children = keywordPair.children

        assert(children.size == 2)

        return children[1] as Quotable
    }

    @JvmStatic
    fun getKeywordValue(noParenthesesKeywordPair: ElixirNoParenthesesKeywordPair): Quotable {
        val children = noParenthesesKeywordPair.children

        assert(children.size == 2)

        return children[1] as Quotable
    }
}
