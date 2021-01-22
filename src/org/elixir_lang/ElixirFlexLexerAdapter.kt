package org.elixir_lang

import com.intellij.lexer.FlexAdapter
import com.intellij.openapi.project.Project

import java.io.Reader

class ElixirFlexLexerAdapter(project: Project?) : FlexAdapter(ElixirFlexLexer(null as Reader?)) {
    var level: Level?
      get() = (flex as ElixirFlexLexer).level
      set(value) = (flex as ElixirFlexLexer).setLevel(value)

    init {
        (flex as ElixirFlexLexer).project = project
    }

    fun stackSize(): Int = (flex as ElixirFlexLexer).stackSize()
}
