package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.annotated_type

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope

object AnnotationType {
    fun toMacroString(annotationType: OtpErlangList): MacroString {
        val annotationMacroString = annotationMacroString(annotationType)
        val typeMacroString = typeMacroString(annotationType)

        return "$annotationMacroString :: $typeMacroString"
    }

    private fun annotationMacroString(annotationType: OtpErlangList) =
            toAnnotation(annotationType)
                    ?.let { annotationToMacroString(it) }
                    ?: "missing_annotation"

    private fun annotationToMacroString(annotation: OtpErlangObject) =
            AbstractCode.toMacroStringDeclaredScope(annotation, Scope.EMPTY).macroString

    private fun toAnnotation(annotationType: OtpErlangList): OtpErlangObject? = annotationType.elementAt(0)
    private fun toType(annotationType: OtpErlangList): OtpErlangObject? = annotationType.elementAt(1)

    private fun typeMacroString(annotationType: OtpErlangList) =
            toType(annotationType)
                    ?.let { typeToMacroString(it) }
                    ?: "missing_type"

    private fun typeToMacroString(type: OtpErlangObject) =
            AbstractCode.toMacroStringDeclaredScope(type, Scope.EMPTY).macroString

}
