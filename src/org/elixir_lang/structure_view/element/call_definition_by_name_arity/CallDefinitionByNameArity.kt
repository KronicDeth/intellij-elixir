package org.elixir_lang.structure_view.element.call_definition_by_name_arity

import org.elixir_lang.NameArity
import org.elixir_lang.structure_view.element.call.Definition

interface CallDefinitionByNameArity : Map<NameArity, Definition> {
    /**
     * Generates a [Definition] for the given `nameArity` if it does not exist.
     *
     * The [Definition] is
     * @param nameArity
     * @return pre-existing [Definition] or new [Definition] add to the `List<TreeElement>`
     */
    fun putNew(nameArity: NameArity): Definition
}
