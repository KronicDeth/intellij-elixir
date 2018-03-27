package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.attribute

import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Attribute
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Attributes
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.ToMacroString

abstract class MacroString(val attribute: Attribute): ToMacroString {
    abstract override fun toMacroString(): String

    companion object {
        fun from(attributes: Attributes, attribute: Attribute): MacroString? =
                Spec.from(attribute) ?:
                Type.from(attributes, attribute)
    }
}
