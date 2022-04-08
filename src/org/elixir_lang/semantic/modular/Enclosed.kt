package org.elixir_lang.semantic.modular

import org.elixir_lang.semantic.Modular

interface Enclosed : MaybeEnclosed {
    override val enclosingModular: Modular
}
