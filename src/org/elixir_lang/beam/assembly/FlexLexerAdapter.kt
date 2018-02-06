package org.elixir_lang.beam.assembly

import com.intellij.lexer.FlexAdapter

import java.io.Reader

class FlexLexerAdapter : FlexAdapter(FlexLexer(null as Reader?))
