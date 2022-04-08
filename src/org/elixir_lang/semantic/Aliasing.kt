package org.elixir_lang.semantic

/**
 * Something that produces an [org.elixir_lang.semantic.Alias], such as `alias` or `require/2`.
 */
interface Aliasing : Semantic {
    val aliases: kotlin.collections.List<Alias>
}
