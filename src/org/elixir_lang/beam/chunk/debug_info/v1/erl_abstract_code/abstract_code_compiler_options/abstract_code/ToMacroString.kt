package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import org.elixir_lang.beam.decompiler.Options

interface ToMacroString {
    fun toMacroString(options: Options): String
}
