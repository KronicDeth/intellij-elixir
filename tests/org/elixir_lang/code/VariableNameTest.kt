package org.elixir_lang.code

import com.ericsson.otp.erlang.OtpErlangAtom
import junit.framework.TestCase

class VariableNameTest : TestCase() {
    fun testSanitizesAtSign() {
        assertEquals("f__1", sanitizeErlangVariableName("f@_1"))
    }

    fun testLowercasesAndEscapesKeyword() {
        assertEquals("erlangVariableFn", sanitizeErlangVariableName("Fn"))
    }

    fun testEscapesNotKeywordVariable() {
        assertEquals("erlangVariableNot", sanitizeErlangVariableName("Not"))
    }

    fun testEscapesEndKeywordVariable() {
        assertEquals("erlangVariableEnd", sanitizeErlangVariableName("End"))
    }

    fun testPreservesTrailingPunctuation() {
        assertEquals("foo!", sanitizeErlangVariableName("Foo!"))
    }

    fun testAtomOverload() {
        assertEquals("left_1", sanitizeErlangVariableName(OtpErlangAtom("left@1")))
    }

    // `_Token`, `_Line` patterns in Erlang code — `_Token` is already valid Elixir
    // (underscore-prefixed unused variable), so it passes through unchanged.
    fun testPreservesUnderscorePrefixedCapitalizedVariable() {
        assertEquals("_Token", sanitizeErlangVariableName("_Token"))
    }

    // Elixir special env vars with double-underscore prefix must not be mutated.
    fun testPreservesElixirSpecialVariable() {
        assertEquals("__CALLER__", sanitizeErlangVariableName("__CALLER__"))
    }
}
