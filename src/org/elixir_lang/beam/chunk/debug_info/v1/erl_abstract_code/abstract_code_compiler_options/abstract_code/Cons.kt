package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode


object Cons {
    fun <T> ifTo(term: OtpErlangObject?, ifTrue: (OtpErlangTuple) -> T): T? = AbstractCode.ifTag(term, TAG, ifTrue)
    fun ifToMacroString(term: OtpErlangObject?): String? = ifTo(term) { toMacroString(it) }
    fun `is`(term: OtpErlangObject?): Boolean = ifTo(term) { true } ?: false

    fun toMacroString(term: OtpErlangTuple): String {
        val headTailMacroString = headTailMacroString(term)

        return "[$headTailMacroString]"
    }

    private const val TAG = "cons"

    private fun headMacroString(term: OtpErlangTuple): String =
            toHead(term)
                    ?.let { headToMacroString(it) }
                    ?: "missing_head"

    private fun headTailMacroString(term: OtpErlangTuple): String {
        val headMacroString = headMacroString(term)
        val tail = toTail(term)

        return when {
            Nil.`is`(tail) -> headMacroString
            Cons.`is`(tail) -> {
                val tailHeadTailMacroString = Cons.headTailMacroString(tail as OtpErlangTuple)

                "$headMacroString, $tailHeadTailMacroString"
            }
            else -> {
                val tailMacroString = tailMacroString(term)

                "$headMacroString | $tailMacroString"
            }
        }
    }


    private fun headToMacroString(term: OtpErlangObject) = AbstractCode.toMacroString(term)

    private fun tailMacroString(term: OtpErlangTuple): String =
            toTail(term)
                    ?.let { tailToMacroString(it) }
                    ?: "missing_tail"

    private fun tailToMacroString(term: OtpErlangObject) = AbstractCode.toMacroString(term)
    private fun toHead(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toTail(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
}
