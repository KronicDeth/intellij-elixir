package org.elixir_lang.semantic.type

enum class Visibility {
    /**
     * Declaration and implementation visible to declaring and using module.
     */
    PUBLIC,
    /**
     * Declaration visible to declaring and using module, but implementation only visible to declaring module.
     */
    OPAQUE,
    /**
     * Declaration only visible to declaring module.
     */
    PRIVATE
}
