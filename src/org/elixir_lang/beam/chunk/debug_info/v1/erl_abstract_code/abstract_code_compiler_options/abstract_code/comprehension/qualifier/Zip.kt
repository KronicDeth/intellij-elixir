package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.comprehension.qualifier

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroStringDeclaredScope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope

/**
 * The `zip` comprehension qualifier introduced in OTP 28 (EEP 73): parallel ("zipped") generators
 * joined with `&&`, as in `A <- As && B <- Bs`.  The abstract form is `{zip, Anno, [Generator, ...]}`,
 * where each `Generator` is a `{Tag, Anno, Pattern, Expression}` tuple - the same shape as the
 * standalone generator of the same kind, and a zip may join generators of different kinds.
 *
 * Elixir has no `&&` zip-generator syntax, so the closest faithful, parseable rendering pairs the
 * generators with [Enum.zip/1](https://hexdocs.pm/elixir/Enum.html#zip/1), which walks a list of
 * enumerables in lockstep and yields one tuple per step (stopping at the shortest, matching
 * zip-generator semantics): the patterns become a tuple pattern and the expressions become the
 * `Enum.zip([...])` argument list - `{Pattern1, Pattern2} <- Enum.zip([Expression1, Expression2])`.
 */
object Zip {
    /** A map generator's pattern is one of these, not an ordinary pattern - see [patternToMacroStringDeclaredScope]. */
    private val MAP_FIELD_TAGS = setOf("map_field_exact", "map_field_assoc")

    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, "zip") { toMacroStringDeclaredScope(it, scope) }

    private fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope {
        val generators = toGenerators(term)

        return if (generators is OtpErlangList && generators.arity() > 0) {
            // Every generator's expression is evaluated in the scope as it stands before any of the
            // zipped patterns bind, mirroring how a standalone generator threads its expression scope
            // into its pattern scope (see Generator).
            val expressionStrings = mutableListOf<String>()
            var expressionScope = scope

            for (generator in generators.elements()) {
                val (expressionMacroString, expressionDeclaredScope) =
                        expressionMacroStringDeclaredScope(generator, expressionScope)
                expressionStrings.add(expressionMacroString.group().string)
                expressionScope = expressionScope.union(expressionDeclaredScope)
            }

            // The patterns then bind in the scope that already includes every generator's expression.
            val patternStrings = mutableListOf<String>()
            var declaredScope = expressionScope

            for (generator in generators.elements()) {
                val (patternMacroString, patternDeclaredScope) =
                        patternMacroStringDeclaredScope(generator, declaredScope)
                patternStrings.add(patternMacroString.string)
                declaredScope = declaredScope.union(patternDeclaredScope)
            }

            val string = "{${patternStrings.joinToString(", ")}} <- " +
                    "Enum.zip([${expressionStrings.joinToString(", ")}])"

            MacroStringDeclaredScope(string, doBlock = false, declaredScope)
        } else {
            MacroStringDeclaredScope.missing("generators", scope, "zip generators", term)
        }
    }

    private fun patternMacroStringDeclaredScope(generator: OtpErlangObject, scope: Scope): MacroStringDeclaredScope =
            (generator as? OtpErlangTuple)
                    ?.let { toPattern(it) }
                    ?.let { patternToMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope.missing("pattern", scope, "zip generator pattern", generator)

    /**
     * A zip may join any kind of generator, and a map generator's pattern is a
     * `{map_field_exact|map_field_assoc, Anno, KeyPattern, ValuePattern}` rather than an ordinary
     * pattern. Rendering that as-is yields `key => value`, which is only valid Elixir inside a `%{}`
     * literal - inside the zip's tuple pattern it does not parse. Iterating a map yields `{key,
     * value}` tuples, so it becomes a two-element tuple pattern, exactly as [MapGenerate] does for a
     * standalone map generator.
     */
    private fun patternToMacroStringDeclaredScope(pattern: OtpErlangObject, scope: Scope): MacroStringDeclaredScope =
            ifTag(pattern, MAP_FIELD_TAGS) { mapField ->
                val (keyMacroString, keyDeclaredScope) =
                        mapFieldPart(mapField, 2, "zip map generator pattern key", scope)
                val valueScope = scope.union(keyDeclaredScope)
                val (valueMacroString, valueDeclaredScope) =
                        mapFieldPart(mapField, 3, "zip map generator pattern value", valueScope)

                MacroStringDeclaredScope(
                        "{${keyMacroString.string}, ${valueMacroString.string}}",
                        doBlock = false,
                        keyDeclaredScope.union(valueDeclaredScope)
                )
            } ?: AbstractCode.toMacroStringDeclaredScope(pattern, scope)

    private fun mapFieldPart(
            mapField: OtpErlangTuple,
            index: Int,
            description: String,
            scope: Scope
    ): MacroStringDeclaredScope =
            mapField.elementAt(index)
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope.missing("pattern", scope, description, mapField)

    private fun expressionMacroStringDeclaredScope(generator: OtpErlangObject, scope: Scope): MacroStringDeclaredScope =
            (generator as? OtpErlangTuple)
                    ?.let { toExpression(it) }
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope.missing("expression", scope, "zip generator expression", generator)

    private fun toGenerators(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toPattern(generator: OtpErlangTuple): OtpErlangObject? = generator.elementAt(2)
    private fun toExpression(generator: OtpErlangTuple): OtpErlangObject? = generator.elementAt(3)
}
