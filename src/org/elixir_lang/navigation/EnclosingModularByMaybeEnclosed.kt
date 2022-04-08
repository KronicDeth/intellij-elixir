package org.elixir_lang.navigation

import org.elixir_lang.semantic.modular.MaybeEnclosed
import org.elixir_lang.structure_view.element.modular.Modular
import java.util.HashMap

/**
 * Keeps track of the enclosing [org.elixir_lang.structure_view.element.modular.Modular] for a
 * [org.elixir_lang.psi.call.Call], so that looking up the
 * [org.elixir_lang.structure_view.element.CallDefinition] for a
 * [org.elixir_lang.structure_view.element.CallDefinitionClause] works correctly in [GotoSymbolContributor]
 */
class EnclosingModularByMaybeEnclosed : HashMap<MaybeEnclosed, Modular?>() {
    /**
     * Generates a [Modular] for the given `call` if it does not exist.
     *
     * @param call
     * @return `null` if `call` is top-level and has no enclosing modular.
     */
    fun putNew(maybeEnclosed: MaybeEnclosed): Modular? {
        val modular: Modular?

        if (containsKey(maybeEnclosed)) {
            modular = get(maybeEnclosed)
        } else {
            modular = maybeEnclosed.enclosingModular?.structureViewTreeElement
            put(maybeEnclosed, modular)
        }

        return modular
    }
}
