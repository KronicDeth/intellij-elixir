package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import junit.framework.TestCase
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.bin_element.TypeSpecifier

class TypeSpecifierTest : TestCase() {
    fun testRendersVariableWidthAsSizeCall() {
        val typeSpecifier = tuple(
            OtpErlangAtom("size"),
            tuple(
                OtpErlangAtom("var"),
                OtpErlangLong(1),
                OtpErlangAtom("Padding")
            )
        )

        assertEquals("size(padding)", TypeSpecifier.toMacroString(typeSpecifier))
    }

    private fun tuple(vararg elements: OtpErlangObject): OtpErlangTuple = OtpErlangTuple(elements)
}
