package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.remote_type.List

object RemoteType {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it) }

    private const val TAG = "remote_type"

    private fun toList(remoteType: OtpErlangTuple) = remoteType.elementAt(2)

    private fun toString(remoteType: OtpErlangTuple): String =
            toList(remoteType)
                    ?.let { List.toString(it) }
                    ?: AbstractCode.missing("remote_type_list", "${TAG} list", remoteType)

    private fun toMacroStringDeclaredScope(remoteType: OtpErlangTuple) =
            MacroStringDeclaredScope(toString(remoteType), doBlock = false, Scope.EMPTY)
}
