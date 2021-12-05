package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.code.Identifier.inspectAsFunction


object Remote {
    fun <T> ifTo(term: OtpErlangObject?, ifTrue: (OtpErlangTuple) -> T) = ifTag(term, TAG, ifTrue)

    fun ifToMacroStringDeclaredScope(term: OtpErlangObject?) =
            ifTo(term) { toMacroStringDeclaredScope(it) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple): MacroStringDeclaredScope {
        val moduleString = moduleString(term)
        val functionString = functionString(term)

        return MacroStringDeclaredScope("$moduleString.$functionString", doBlock = false, Scope.EMPTY)
    }

    private const val TAG = "remote"

    private fun functionString(term: OtpErlangTuple): String =
            toFunction(term)
                    ?.let { functionToString(it) }
                    ?: AbstractCode.missing("function", "${TAG} function", term)

    private fun functionToString(function: OtpErlangObject): String =
            Atom.toElixirAtom(function)
                    ?.let { inspectAsFunction(it) }
                    ?: AbstractCode.unknown("function", "${TAG} function", function)

    internal fun moduleString(term: OtpErlangTuple): String =
            toModule(term)
                    ?.let { moduleToString(it) }
                    ?: AbstractCode.error(":missing_module", "${TAG} module", term)

    private fun moduleToString(module: OtpErlangObject) = AbstractCode.toString(module)

    internal fun toFunction(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
    private fun toModule(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
