package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.clause

import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroStringDeclaredScope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Sequence

object PatternSequence {
    fun toMacroStringDeclaredScope(term: OtpErlangObject?, scope: Scope): MacroStringDeclaredScope =
            Sequence.toMacroStringDeclaredScope(term, scope, ", ")
}
