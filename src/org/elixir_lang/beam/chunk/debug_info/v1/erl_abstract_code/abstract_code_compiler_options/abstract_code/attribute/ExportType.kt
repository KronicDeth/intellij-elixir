package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.attribute

import com.ericsson.otp.erlang.OtpErlangList
import org.elixir_lang.NameArity
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Attribute

private const val ATTRIBUTE_NAME = "export_type"

object ExportType {
    fun `is`(attribute: Attribute) = attribute.name == ATTRIBUTE_NAME

    fun nameAritySet(attribute: Attribute): Set<NameArity> =
            (attribute.value as? OtpErlangList)?.mapNotNull { NameArity.Companion.from(it) }?.toSet() ?:
            emptySet()
}
