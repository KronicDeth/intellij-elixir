package org.elixir_lang

import com.intellij.lexer.LookAheadLexer
import com.intellij.lexer.MergingLexerAdapter
import com.intellij.psi.tree.TokenSet
import org.elixir_lang.psi.ElixirTypes

class ElixirLexer : LookAheadLexer(MergingLexerAdapter(ElixirFlexLexerAdapter(), FRAGMENTS)) {
    companion object {
        val FRAGMENTS = TokenSet.create(
                ElixirTypes.ATOM_FRAGMENT,
                ElixirTypes.CHAR_LIST_FRAGMENT,
                ElixirTypes.REGEX_FRAGMENT,
                ElixirTypes.SIGIL_FRAGMENT,
                ElixirTypes.STRING_FRAGMENT,
                ElixirTypes.WORDS_FRAGMENT
        )
    }
}
