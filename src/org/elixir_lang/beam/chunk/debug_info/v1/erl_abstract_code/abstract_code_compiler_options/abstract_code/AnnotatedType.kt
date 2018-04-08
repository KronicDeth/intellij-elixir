package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.annotated_type.AnnotationType

object AnnotatedType {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it) }

    private const val TAG = "ann_type"

    private fun annotationTypeToMacroString(annotationType: OtpErlangObject) =
            when (annotationType) {
                is OtpErlangList -> AnnotationType.toMacroString(annotationType)
                else -> "unknown_annotation_type"
            }

    private fun toMacroString(annotatedType: OtpErlangTuple): MacroString =
            toAnnotationType(annotatedType)
                    ?.let { annotationTypeToMacroString(it) }
                    ?: "missing_annotation_type"

    private fun toMacroStringDeclaredScope(annotatedType: OtpErlangTuple) =
            MacroStringDeclaredScope(toMacroString(annotatedType), Scope.EMPTY)

    private fun toAnnotationType(annotatedType: OtpErlangTuple): OtpErlangObject? = annotatedType.elementAt(2)

}
