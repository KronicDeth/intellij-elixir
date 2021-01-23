package org.elixir_lang

import com.intellij.lexer.LookAheadLexer
import com.intellij.lexer.MergingLexerAdapter
import com.intellij.openapi.project.Project
import com.intellij.psi.tree.TokenSet
import org.elixir_lang.psi.ElixirTypes

class ElixirLexer(private val elixirFlexLexerAdapter: ElixirFlexLexerAdapter) :
        LookAheadLexer(MergingLexerAdapter(elixirFlexLexerAdapter, FRAGMENTS)) {
    constructor(project: Project?): this(ElixirFlexLexerAdapter(project))
    constructor(): this(null)

    var level: Level?
      get() = elixirFlexLexerAdapter.level
      set(value) {
          elixirFlexLexerAdapter.level = value
      }

    fun stackSize(): Int = elixirFlexLexerAdapter.stackSize()

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
