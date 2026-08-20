package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import junit.framework.TestCase

class MapTest : TestCase() {

    /**
     * Erlang: `#{Var | Key => Value}` — a map update with one association.
     * Expected Elixir: `%{var | key => value}`
     */
    fun testUpdateWithAssociationsRendersMapUpdate() {
        val line = OtpErlangLong(1)
        // source variable: {var, 1, 'Stat'}
        val sourceVar = tuple(OtpErlangAtom("var"), line, OtpErlangAtom("Stat"))
        // association: {map_field_assoc, 1, {atom, 1, key}, {atom, 1, value}}
        val assoc = tuple(
            OtpErlangAtom("map_field_assoc"), line,
            tuple(OtpErlangAtom("atom"), line, OtpErlangAtom("key")),
            tuple(OtpErlangAtom("atom"), line, OtpErlangAtom("value"))
        )
        // map update: {map, 1, {var, 1, 'Stat'}, [{map_field_assoc, ...}]}
        val mapUpdate = tuple(
            OtpErlangAtom("map"), line,
            sourceVar,
            OtpErlangList(arrayOf<OtpErlangObject>(assoc))
        )

        val result = Map.toMacroStringDeclaredScope(mapUpdate, Scope.EMPTY).macroString.string

        assertTrue("Expected map update syntax with pipe, got: $result", result.contains("|"))
        assertTrue("Expected %{ prefix, got: $result", result.startsWith("%{"))
    }

    /**
     * Erlang: `#{Var | }` — a map update with an empty association list.
     * This is valid Erlang abstract code but `%{stat | }` is a syntax error in Elixir.
     * Expected: the source expression only (no `%{... | }`).
     */
    fun testUpdateWithEmptyAssociationsOmitsMapUpdateSyntax() {
        val line = OtpErlangLong(1)
        // source variable: {var, 1, 'Stat'}
        val sourceVar = tuple(OtpErlangAtom("var"), line, OtpErlangAtom("Stat"))
        // map update with empty association list: {map, 1, {var, 1, 'Stat'}, []}
        val mapUpdate = tuple(
            OtpErlangAtom("map"), line,
            sourceVar,
            OtpErlangList(arrayOf<OtpErlangObject>())
        )

        val result = Map.toMacroStringDeclaredScope(mapUpdate, Scope.EMPTY).macroString.string

        // Must NOT contain the pipe operator — that would produce `%{stat | }` which is invalid Elixir
        assertFalse("Should not contain '|' for empty associations, got: $result", result.contains("|"))
        // Must NOT contain `%{` wrapper since there are no associations to update
        assertFalse("Should not contain '%{' for empty associations, got: $result", result.startsWith("%{"))
    }

    /**
     * Erlang: `#{}` — a map construction with no associations.
     * Expected Elixir: `%{}`
     */
    fun testConstructionWithEmptyAssociationsRendersEmptyMap() {
        val line = OtpErlangLong(1)
        // map construction: {map, 1, []}
        val mapConstruction = tuple(
            OtpErlangAtom("map"), line,
            OtpErlangList(arrayOf<OtpErlangObject>())
        )

        val result = Map.toMacroStringDeclaredScope(mapConstruction, Scope.EMPTY).macroString.string

        assertEquals("%{}", result)
    }

    private fun tuple(vararg elements: OtpErlangObject): OtpErlangTuple = OtpErlangTuple(elements)
}
