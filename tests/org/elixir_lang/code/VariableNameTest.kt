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

    fun testPreservesTrailingPunctuation() {
        assertEquals("foo!", sanitizeErlangVariableName("Foo!"))
    }

    fun testAtomOverload() {
        assertEquals("left_1", sanitizeErlangVariableName(OtpErlangAtom("left@1")))
    }
}
