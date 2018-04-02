package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.code.Identifier.inspectAsFunction


object Remote {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject?) =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple): MacroStringDeclaredScope {
        val moduleMacroString = moduleMacroString(term)
        val functionMacroString = functionMacroString(term)

        return MacroStringDeclaredScope("$moduleMacroString.$functionMacroString", Scope.EMPTY)
    }

    private const val TAG = "remote"

    private fun functionMacroString(term: OtpErlangTuple): MacroString =
            toFunction(term)
                    ?.let { functionToMacroString(it) }
                    ?: "missing_function"

    private fun functionToMacroString(function: OtpErlangObject): MacroString =
            Atom.toElixirAtom(function)
                    ?.let { inspectAsFunction(it) }
                    ?: "unknown_function"

    private fun moduleMacroString(term: OtpErlangTuple): MacroString =
            toModule(term)
                    ?.let { moduleToMacroString(it) }
                    ?: ":missing_module"

    private fun moduleToMacroString(module: OtpErlangObject) =
            AbstractCode.toMacroStringDeclaredScope(module, Scope.EMPTY).macroString

    private fun toFunction(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
    private fun toModule(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
