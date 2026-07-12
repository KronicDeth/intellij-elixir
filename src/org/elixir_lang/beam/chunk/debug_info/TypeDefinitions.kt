package org.elixir_lang.beam.chunk.debug_info

import org.elixir_lang.beam.Beam
import org.elixir_lang.beam.chunk.Atoms
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.AbstractCodeCompileOptions
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.attribute.Type
import org.elixir_lang.beam.psi.stubs.ModuleStub
import org.elixir_lang.beam.type.VisibilityNameArity
import org.elixir_lang.model.psi.type.TypeBuiltins.BUILTIN_ARITY_BY_NAME
import org.elixir_lang.psi.Protocol
import org.elixir_lang.type.Visibility
import java.util.*

object TypeDefinitions {
    fun visibilityNameAritySortedSetByVisibility(
        parentStub: ModuleStub<*>,
        beam: Beam,
        atoms: Atoms
    ): Map<Visibility, SortedSet<VisibilityNameArity>> =
        when {
            // Built-in types are faked as being defined in `erlang`, so built-in type resolution points at a
            // single location.
            atoms.moduleName() == "erlang" -> builtinVisibilityNameAritySortedSetByVisibility()
            // Protocol type (`t`) definitions are handled specially during resolution, not as beam type stubs.
            Protocol.`is`(parentStub) -> emptyMap()
            else -> abstractCodeVisibilityNameAritySortedSetByVisibility(beam)
        }

    private fun builtinVisibilityNameAritySortedSetByVisibility(): Map<Visibility, SortedSet<VisibilityNameArity>> {
        val sortedSet = TreeSet<VisibilityNameArity>()

        for ((name, arities) in BUILTIN_ARITY_BY_NAME) {
            for (arity in arities) {
                sortedSet.add(VisibilityNameArity(Visibility.PUBLIC, name, arity))
            }
        }

        return mapOf(Visibility.PUBLIC to sortedSet)
    }

    /**
     * Enumerates a module's `@type`/`@opaque`/`@typep` definitions from the Erlang abstract code, the same source
     * and with the same name/arity/visibility the decompiler renders them from (see `Decompiler.appendTypes` and
     * `attribute.Type`).  `ModuleImpl.setMirror` pairs each type stub to the decompiled mirror `@type` call by name
     * and arity, so the produced stub `(name, arity)` set must equal what the mirror declares; deriving both from
     * `attribute.Type` keeps them consistent by construction.
     *
     * Beams without Erlang abstract code (e.g. Elixir-compiled beams whose debug info is `elixir_erl`) get no type
     * stubs here; their types are not indexed for resolution, matching prior behavior.
     */
    private fun abstractCodeVisibilityNameAritySortedSetByVisibility(beam: Beam):
            Map<Visibility, SortedSet<VisibilityNameArity>> {
        val abstractCodeCompileOptions = beam.debugInfo() as? AbstractCodeCompileOptions ?: return emptyMap()

        return abstractCodeCompileOptions
            .attributes
            .macroStringAttributes
            .filterIsInstance<Type>()
            .mapNotNull { type ->
                type.name?.let { name -> VisibilityNameArity(visibility(type), name, type.arity) }
            }
            .groupBy(VisibilityNameArity::visibility)
            .mapValues { (_, visibilityNameArityList) -> TreeSet(visibilityNameArityList) }
    }

    private fun visibility(type: Type): Visibility =
        when (type.elixirAttributeName) {
            Visibility.OPAQUE.moduleAttribute -> Visibility.OPAQUE
            Visibility.PRIVATE.moduleAttribute -> Visibility.PRIVATE
            else -> Visibility.PUBLIC
        }
}
