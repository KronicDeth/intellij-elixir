package org.elixir_lang

import com.intellij.lexer.FlexAdapter

import java.io.Reader

class ElixirFlexLexerAdapter : FlexAdapter(ElixirFlexLexer(null as Reader?))
