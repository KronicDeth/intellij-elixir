package org.elixir_lang.semantic.modular

import org.elixir_lang.semantic.Modular

interface MaybeEnclosed {
    val enclosingModular: Modular?
}
