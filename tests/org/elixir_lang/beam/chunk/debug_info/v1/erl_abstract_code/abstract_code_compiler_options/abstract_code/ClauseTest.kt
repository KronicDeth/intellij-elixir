package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import junit.framework.TestCase

class ClauseTest : TestCase() {

    /**
     * Erlang: `fun() when N =:= 16 -> ok end`
     * A zero-argument fun clause with a guard.
     *
     * Without the fix this produces `fn  when n === 16 ->` which is a syntax error
     * because `when` is a reserved word and can't appear directly after `fn`.
     *
     * Expected: the clause string starts with `()` so it becomes `fn () when ... ->`.
     */
    fun testZeroArgClauseWithGuardEmitsParentheses() {
        val line = OtpErlangLong(1)

        // Guard: [[{op, 1, '=:=', {var, 1, 'N'}, {integer, 1, 16}}]]
        val guard = OtpErlangList(arrayOf<OtpErlangObject>(
            OtpErlangList(arrayOf<OtpErlangObject>(
                tuple(
                    OtpErlangAtom("op"), line, OtpErlangAtom("=:="),
                    tuple(OtpErlangAtom("var"), line, OtpErlangAtom("N")),
                    tuple(OtpErlangAtom("integer"), line, OtpErlangLong(16))
                )
            ))
        ))

        // Body: [{atom, 1, ok}]
        val body = OtpErlangList(arrayOf<OtpErlangObject>(
            tuple(OtpErlangAtom("atom"), line, OtpErlangAtom("ok"))
        ))

        // Clause: {clause, 1, [], Guard, Body}  — zero patterns, guard present
        val clause = tuple(
            OtpErlangAtom("clause"), line,
            OtpErlangList(arrayOf<OtpErlangObject>()),  // empty pattern list
            guard,
            body
        )

        val result = Clause.toString(clause, Scope.EMPTY)

        assertTrue(
            "Zero-arg clause with guard should start with '() when', got: $result",
            result.startsWith("() when")
        )
        assertFalse(
            "Should not have bare 'when' without parentheses at start, got: $result",
            result.matches(Regex("^\\s*when\\b.*"))
        )
    }

    /**
     * Erlang: `fun() -> ok end`
     * A zero-argument fun clause without a guard.
     *
     * Even without a guard, `()` is emitted so that multi-clause fns don't
     * produce a bare `->` which is a syntax error. `fn () -> :ok end` is valid Elixir.
     */
    fun testZeroArgClauseWithoutGuardEmitsParentheses() {
        val line = OtpErlangLong(1)

        // Empty guard sequence: []
        val noGuard = OtpErlangList(arrayOf<OtpErlangObject>())

        // Body: [{atom, 1, ok}]
        val body = OtpErlangList(arrayOf<OtpErlangObject>(
            tuple(OtpErlangAtom("atom"), line, OtpErlangAtom("ok"))
        ))

        // Clause: {clause, 1, [], [], Body}  — zero patterns, no guard
        val clause = tuple(
            OtpErlangAtom("clause"), line,
            OtpErlangList(arrayOf<OtpErlangObject>()),  // empty pattern list
            noGuard,
            body
        )

        val result = Clause.toString(clause, Scope.EMPTY)

        // Should start with "()" so multi-clause fns don't have a bare "->"
        assertTrue(
            "Zero-arg clause without guard should start with '()', got: $result",
            result.startsWith("()")
        )
    }

    /**
     * Erlang: `fun(X) when X > 0 -> ok end`
     * A single-argument clause with a guard.
     *
     * Expected: patterns present, so no extra `()` wrapping — just `x when x > 0 ->`.
     */
    fun testSingleArgClauseWithGuardDoesNotAddExtraParentheses() {
        val line = OtpErlangLong(1)

        // Pattern: [{var, 1, 'X'}]
        val patterns = OtpErlangList(arrayOf<OtpErlangObject>(
            tuple(OtpErlangAtom("var"), line, OtpErlangAtom("X"))
        ))

        // Guard: [[{op, 1, '>', {var, 1, 'X'}, {integer, 1, 0}}]]
        val guard = OtpErlangList(arrayOf<OtpErlangObject>(
            OtpErlangList(arrayOf<OtpErlangObject>(
                tuple(
                    OtpErlangAtom("op"), line, OtpErlangAtom(">"),
                    tuple(OtpErlangAtom("var"), line, OtpErlangAtom("X")),
                    tuple(OtpErlangAtom("integer"), line, OtpErlangLong(0))
                )
            ))
        ))

        // Body: [{atom, 1, ok}]
        val body = OtpErlangList(arrayOf<OtpErlangObject>(
            tuple(OtpErlangAtom("atom"), line, OtpErlangAtom("ok"))
        ))

        // Clause: {clause, 1, [{var,1,'X'}], Guard, Body}
        val clause = tuple(
            OtpErlangAtom("clause"), line,
            patterns,
            guard,
            body
        )

        val result = Clause.toString(clause, Scope.EMPTY)

        assertFalse(
            "Single-arg clause should not start with '()', got: $result",
            result.startsWith("()")
        )
        assertTrue(
            "Single-arg clause with guard should contain 'when', got: $result",
            result.contains("when")
        )
    }

    private fun tuple(vararg elements: OtpErlangObject): OtpErlangTuple = OtpErlangTuple(elements)
}
