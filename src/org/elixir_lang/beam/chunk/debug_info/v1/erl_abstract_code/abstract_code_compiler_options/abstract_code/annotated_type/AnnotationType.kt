package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.annotated_type

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope

object AnnotationType {
    fun toString(annotationType: OtpErlangList): String {
        val annotationString = annotationString(annotationType)
        val typeString = typeString(annotationType)

        return "$annotationString :: $typeString"
    }

    private fun annotationString(annotationType: OtpErlangList): String =
            toAnnotation(annotationType)
                    ?.let { annotationToString(it) }
                    ?: "missing_annotation"

    private fun annotationToString(annotation: OtpErlangObject): String = AbstractCode.toString(annotation)

    private fun toAnnotation(annotationType: OtpErlangList): OtpErlangObject? = annotationType.elementAt(0)
    private fun toType(annotationType: OtpErlangList): OtpErlangObject? = annotationType.elementAt(1)

    private fun typeString(annotationType: OtpErlangList): String =
            toType(annotationType)
                    ?.let { typeToString(it) }
                    ?: "missing_type"

    private fun typeToString(type: OtpErlangObject): String = AbstractCode.toString(type)
}
