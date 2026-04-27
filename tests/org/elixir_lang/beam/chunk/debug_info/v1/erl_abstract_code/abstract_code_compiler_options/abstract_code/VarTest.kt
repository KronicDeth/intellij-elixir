package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import junit.framework.TestCase

class VarTest : TestCase() {
    fun testSanitizesAtSignInVariableName() {
        assertEquals("f__1", Var.nameToString(OtpErlangAtom("f@_1")))
    }

    fun testSanitizesAtSignInMiddleOfVariableName() {
        assertEquals("left_1", Var.nameToString(OtpErlangAtom("left@1")))
    }

    fun testEscapesKeywordAfterNormalization() {
        assertEquals("erlangVariableFn", Var.nameToString(OtpErlangAtom("Fn")))
    }

    fun testPreservesValidTrailingPunctuation() {
        assertEquals("foo!", Var.nameToString(OtpErlangAtom("Foo!")))
    }

    // `_Token`, `_Line` are valid Elixir unused-variable syntax — pass through unchanged.
    fun testPreservesUnderscorePrefixedCapitalizedVariable() {
        assertEquals("_Token", Var.nameToString(OtpErlangAtom("_Token")))
    }

    // Elixir special env variables like __CALLER__ must not be modified.
    fun testPreservesElixirDoubleUnderscoreVariable() {
        assertEquals("__CALLER__", Var.nameToString(OtpErlangAtom("__CALLER__")))
    }
}
