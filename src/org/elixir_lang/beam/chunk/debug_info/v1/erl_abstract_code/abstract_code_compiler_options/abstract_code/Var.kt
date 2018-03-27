package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple

private const val TAG = "var"
private const val DEFAULT_NAME_TO_MACRO_STRING = "..."

class Var(term: OtpErlangTuple): Node(term) {
    val name by lazy { term.elementAt(2) as? OtpErlangAtom }

    override fun toMacroString(): String = nameToMacroString(name)

    companion object {
        fun nameToMacroString(name: OtpErlangObject?) =
                when (name) {
                    is OtpErlangAtom? -> nameToMacroString(name)
                    else -> DEFAULT_NAME_TO_MACRO_STRING
                }

        fun nameToMacroString(name: OtpErlangAtom?) =
                name?.let { it.atomValue().decapitalize() } ?: DEFAULT_NAME_TO_MACRO_STRING

        fun from(term: OtpErlangObject) = ifTag(term, TAG, ::Var)
    }
}
