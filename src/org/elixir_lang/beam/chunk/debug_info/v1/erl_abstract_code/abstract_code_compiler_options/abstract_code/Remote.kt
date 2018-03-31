package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.code.Identifier.inspectAsFunction


object Remote {
    fun ifToMacroString(term: OtpErlangObject?) = AbstractCode.ifTag(term, TAG) { toMacroString(it) }

    fun toMacroString(term: OtpErlangTuple): String {
        val moduleMacroString = moduleMacroString(term)
        val functionMacroString = functionMacroString(term)

        return "$moduleMacroString.$functionMacroString"
    }

    private const val TAG = "remote"

    private fun functionMacroString(term: OtpErlangTuple): String =
            toFunction(term)
                    ?.let { functionToMacroString(it) }
                    ?: "missing_function"

    private fun functionToMacroString(function: OtpErlangObject): String =
            Atom.toElixirAtom(function)
                    ?.let { inspectAsFunction(it) }
                    ?: "unknown_function"

    private fun moduleMacroString(term: OtpErlangTuple): String =
            toModule(term)
                    ?.let { moduleToMacroString(it) }
                    ?: ":missing_module"

    private fun moduleToMacroString(module: OtpErlangObject) = AbstractCode.toMacroString(module)

    private fun toFunction(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
    private fun toModule(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
