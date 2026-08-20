package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import junit.framework.TestCase

class RecordFieldTest : TestCase() {
    fun testRendersWildcardVarFieldWithoutUnknownFieldFallback() {
        val line = OtpErlangLong(1)
        val wildcardVarField = tuple(
            OtpErlangAtom("var"),
            line,
            OtpErlangAtom("_")
        )
        val undefinedAtom = tuple(
            OtpErlangAtom("atom"),
            line,
            OtpErlangAtom("undefined")
        )
        val recordField = tuple(
            OtpErlangAtom("record_field"),
            line,
            wildcardVarField,
            undefinedAtom
        )

        val macroString = RecordField.toMacroStringDeclaredScope(recordField, Scope.EMPTY).macroString.string

        assertEquals("_: :undefined", macroString)
    }

    private fun tuple(vararg elements: OtpErlangObject): OtpErlangTuple = OtpErlangTuple(elements)
}
