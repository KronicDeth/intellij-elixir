package org.elixir_lang.credo.state

import com.intellij.util.xmlb.annotations.Attribute

data class Env(@Attribute("name") var name: String = "", @Attribute("value") var value: String = "") : Comparable<Env> {
    override fun compareTo(other: Env): Int = compareValuesBy(this, other, Env::name, Env::value)
}
