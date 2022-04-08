package org.elixir_lang.semantic.implementation

import org.elixir_lang.CanonicallyNamed
import org.elixir_lang.semantic.Semantic

interface ForName : CanonicallyNamed, Semantic {
    val implementation: org.elixir_lang.semantic.Implementation
    val protocolName: String
    val forName: String
}
