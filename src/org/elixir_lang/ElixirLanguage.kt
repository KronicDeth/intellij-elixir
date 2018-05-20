package org.elixir_lang

import com.intellij.lang.Language

object ElixirLanguage: Language("Elixir") {
    override fun isCaseSensitive(): Boolean = true
}
