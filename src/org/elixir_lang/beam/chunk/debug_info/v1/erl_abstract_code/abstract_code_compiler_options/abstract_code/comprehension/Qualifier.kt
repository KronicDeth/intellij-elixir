package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.comprehension

import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroStringDeclaredScope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.comprehension.qualifier.BitstringGenerate
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.comprehension.qualifier.Generate
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.comprehension.qualifier.MapGenerate

object Qualifier {
    fun toMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope =
            Generate.ifToMacroStringDeclaredScope(term, scope) ?:
            BitstringGenerate.ifToMacroStringDeclaredScope(term, scope) ?:
            MapGenerate.ifToMacroStringDeclaredScope(term, scope) ?:
            AbstractCode.toMacroStringDeclaredScope(term, scope)
}
