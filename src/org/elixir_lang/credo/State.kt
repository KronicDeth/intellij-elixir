package org.elixir_lang.credo

import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Tag
import org.elixir_lang.credo.state.EnvironmentVariablesData

class State {
    @Attribute("include-explanation")
    var includeExplanation = true

    @Tag("elixir-arguments")
    var elixirArguments = ""

    @Tag("erl-arguments")
    var erlArguments = ""

    @Tag("environment-variable-data")
    var environmentVariableData = EnvironmentVariablesData()
}
