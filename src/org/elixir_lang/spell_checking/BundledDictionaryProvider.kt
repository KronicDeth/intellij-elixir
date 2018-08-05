package org.elixir_lang.spell_checking

class BundledDictionaryProvider : com.intellij.spellchecker.BundledDictionaryProvider {
    override fun getBundledDictionaries(): Array<String> =
            arrayOf(
                    "/elixir.dic",
                    "/elixir/ecto.dic"
                    )
}
