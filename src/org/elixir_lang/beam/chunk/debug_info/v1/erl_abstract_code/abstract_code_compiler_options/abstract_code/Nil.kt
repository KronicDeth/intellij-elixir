package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag

object Nil {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject?): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it) }

    fun `is`(term: OtpErlangObject?): Boolean = AbstractCode.ifTag(term, TAG) { true } ?: false

    fun toMacroStringDeclaredScope(@Suppress("UNUSED_PARAMETER") term: OtpErlangObject) =
            MacroStringDeclaredScope("[]", Scope.EMPTY)

    private const val TAG = "nil"
}
