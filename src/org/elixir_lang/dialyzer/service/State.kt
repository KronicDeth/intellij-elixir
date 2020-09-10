package org.elixir_lang.dialyzer.service

import com.intellij.util.xmlb.annotations.Tag

class State(@Tag("mixDialyzerCommand") var mixDialyzerCommand: String = "mix dialyzer")
