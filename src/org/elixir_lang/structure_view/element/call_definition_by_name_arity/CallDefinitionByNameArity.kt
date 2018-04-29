package org.elixir_lang.structure_view.element.call_definition_by_name_arity

import org.elixir_lang.NameArity
import org.elixir_lang.structure_view.element.CallDefinition

interface CallDefinitionByNameArity : Map<NameArity, CallDefinition> {
    /**
     * Generates a [CallDefinition] for the given `nameArity` if it does not exist.
     *
     * The [CallDefinition] is
     * @param nameArity
     * @return pre-existing [CallDefinition] or new [CallDefinition] add to the `List<TreeElement>`
     */
    fun putNew(nameArity: NameArity): CallDefinition
}
