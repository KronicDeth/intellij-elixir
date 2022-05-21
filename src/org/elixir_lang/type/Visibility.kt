package org.elixir_lang.type

enum class Visibility(val moduleAttribute: String) {
    PUBLIC("type"),
    OPAQUE("opaque"),
    PRIVATE("typep")
}
