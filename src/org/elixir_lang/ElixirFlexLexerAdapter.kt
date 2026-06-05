package org.elixir_lang

import com.intellij.lexer.FlexAdapter
import com.intellij.openapi.project.Project

import java.io.Reader

class ElixirFlexLexerAdapter(project: Project?) : FlexAdapter(ElixirFlexLexer(null as Reader?)) {
    init {
        flex.project = project
    }

    override fun getFlex(): ElixirFlexLexer = super.getFlex() as ElixirFlexLexer

    /**
     * Clears the [ElixirFlexLexer] state stack before each lex so that leftover
     * `pushAndBegin`/`popAndBegin` state from a previous document cannot corrupt the next one.
     *
     * This is the canonical place for this reset: [ElixirFlexLexer] is JFlex-generated and
     * cannot be customised to clear the stack automatically on `reset()`.
     */
    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        flex.clearStack()
        super.start(buffer, startOffset, endOffset, initialState)
    }

    fun stackSize(): Int = flex.stackSize()
}
