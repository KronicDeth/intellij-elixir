package org.elixir_lang.beam.chunk.debug_info

import org.elixir_lang.beam.Beam
import org.elixir_lang.beam.chunk.Atoms
import org.elixir_lang.beam.type.VisibilityNameArity
import org.elixir_lang.reference.resolver.Type
import org.elixir_lang.type.Visibility
import java.util.*

object TypeDefinitions {
    fun visibilityNameAritySortedSetByVisibility(beam: Beam, atoms: Atoms): Map<Visibility,
            SortedSet<VisibilityNameArity>> =
        if (atoms.moduleName() == "erlang") {
            val sortedSet = TreeSet<VisibilityNameArity>()

            for ((name, arities) in Type.BUILTIN_ARITY_BY_NAME) {
                for (arity in arities) {
                    sortedSet.add(VisibilityNameArity(Visibility.PUBLIC, name, arity))
                }
            }

            mapOf(Visibility.PUBLIC to sortedSet)
        } else {
            emptyMap()
        }
}
