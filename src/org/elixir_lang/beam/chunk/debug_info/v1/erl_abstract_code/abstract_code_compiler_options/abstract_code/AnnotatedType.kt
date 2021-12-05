package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.annotated_type.AnnotationType

object AnnotatedType {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it) }

    private const val TAG = "ann_type"

    private fun annotationTypeToString(annotationType: OtpErlangObject): String =
            when (annotationType) {
                is OtpErlangList -> AnnotationType.toString(annotationType)
                else -> AbstractCode.unknown("annotation_type", "ann_type annotation type", annotationType)
            }

    private fun toString(annotatedType: OtpErlangTuple): String =
            toAnnotationType(annotatedType)
                    ?.let { annotationTypeToString(it) }
                    ?: AbstractCode.missing("annotation_type", "ann_type annotation type", annotatedType)

    private fun toMacroStringDeclaredScope(annotatedType: OtpErlangTuple) =
            MacroStringDeclaredScope(toString(annotatedType), doBlock = false, Scope.EMPTY)

    private fun toAnnotationType(annotatedType: OtpErlangTuple): OtpErlangObject? = annotatedType.elementAt(2)

}
