package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.*

private const val TAG = "function"

class Function(term: OtpErlangTuple, attributes: Attributes): Node(term) {
    val name by lazy { term.elementAt(2) as? OtpErlangAtom }
    val arity by lazy { (term.elementAt(3) as? OtpErlangLong)?.longValue() }
    val clauses by lazy {
        (term.elementAt(4) as? OtpErlangList)?.let {
            it
                    .asSequence()
                    .filterIsInstance<OtpErlangTuple>()
                    .mapNotNull { Clause.from(it, attributes,this) }
                    .toList()
        } ?: emptyList()
    }

    override fun toMacroString(): String = clauses.joinToString("\n\n", transform = Clause::toMacroString)

    companion object {
        fun from(term: OtpErlangObject, attributes: Attributes): Function? =
                ifTag(term, TAG) { Function(it, attributes) }
    }
}
