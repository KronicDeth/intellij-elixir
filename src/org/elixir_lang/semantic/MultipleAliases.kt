package org.elixir_lang.semantic

/**
 * Appears in argument to `alias/1`: `alias Foo.{Bar, Baz, Biz}`.  `Foo.{Bar, Baz, Biz}` is the [MultipleAliases]
 */
interface MultipleAliases : Semantic {
    val aliases: kotlin.collections.List<Alias>
    val suffixes: kotlin.collections.List<Alias>
}
