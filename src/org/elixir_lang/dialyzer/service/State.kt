package org.elixir_lang.dialyzer.service

import com.intellij.util.xmlb.annotations.Tag

// Match same arguments as a Mix Run Configuration, but without the values that will be adjusted by the AnalysisScope,
// like directory or module.
class State(@Tag("mixArguments") var mixArguments: String = "dialyzer",
            @Tag("elixirArguments") var elixirArguments: String = "",
            @Tag("erlArguments") var erlArguments: String = "")
