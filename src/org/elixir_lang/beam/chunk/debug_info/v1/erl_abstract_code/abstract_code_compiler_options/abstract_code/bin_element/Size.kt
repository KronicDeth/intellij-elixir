package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.bin_element

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode

object Size {
    fun isDefault(term: OtpErlangObject): Boolean = (term as? OtpErlangAtom?)?.atomValue() == default

    fun toString(term: OtpErlangAtom): String? =
            if (term.atomValue() == default) {
                null
            } else {
                AbstractCode.unknown("size_atom", "bin element size atom", term)
            }

    fun toString(term: OtpErlangObject): String? =
            when (term) {
                is OtpErlangAtom -> toString(term)
                else -> {
                    val sizeString = AbstractCode.toString(term)

                    // In Elixir bitstring syntax, integer literal sizes can be used directly
                    // (e.g. `x :: 8`), but variable or expression sizes must be wrapped with
                    // `size()` — e.g. `0 :: size(padding)` not `0 :: padding`, which would
                    // be interpreted as a type specifier rather than a size.
                    if (isIntegerLiteral(term)) {
                        sizeString
                    } else {
                        "size($sizeString)"
                    }
                }
            }

    private const val default = "default"

    private fun isIntegerLiteral(term: OtpErlangObject): Boolean =
            when (term) {
                is OtpErlangLong -> true
                is OtpErlangTuple -> {
                    // Abstract code integer: {integer, Line, Value}
                    val tag = term.elementAt(0)
                    tag is OtpErlangAtom && tag.atomValue() == "integer"
                }
                else -> false
            }
}
