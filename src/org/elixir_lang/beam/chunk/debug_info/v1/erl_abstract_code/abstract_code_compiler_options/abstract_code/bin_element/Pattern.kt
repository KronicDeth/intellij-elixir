package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.bin_element

import com.ericsson.otp.erlang.OtpErlangBinary
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.AbstractCodeString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroStringDeclaredScope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope
import org.elixir_lang.beam.term.inspect

object Pattern {
    fun toElixirString(term: OtpErlangObject): OtpErlangBinary? = AbstractCodeString.toElixirString(term)

    /**
     * Elixir does not have `<<'charlist'>>`, but `<<"string">>`
     */
    fun toMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope =
            toElixirString(term)
                    ?.let {
                        inspect(it).let { MacroStringDeclaredScope(it, Scope.EMPTY) }
                    }
                    ?: AbstractCode.toMacroStringDeclaredScope(term, scope)
}
