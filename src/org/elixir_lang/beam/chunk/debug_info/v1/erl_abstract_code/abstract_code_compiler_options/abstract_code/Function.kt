package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.*
import org.elixir_lang.Visibility
import org.elixir_lang.beam.MacroNameArity
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.function.Clause
import org.elixir_lang.beam.decompiler.Default
import org.elixir_lang.beam.decompiler.Unquoted

private const val TAG = "function"

class Function(term: OtpErlangTuple, attributes: Attributes): Node(term) {
    val name by lazy { term.elementAt(2) as? OtpErlangAtom }
    val callDefinitionName by lazy {
        name?.decompileAsCallDefinitionName()
    }
    val arity by lazy { (term.elementAt(3) as? OtpErlangLong)?.longValue() }
    val clauses by lazy {
        (term.elementAt(4) as? OtpErlangList)?.let {
            it
                    .asSequence()
                    .filterIsInstance<OtpErlangTuple>()
                    .mapNotNull { Clause.from(it, attributes, this) }
                    .toList()
        } ?: emptyList()
    }

    override fun toMacroString(): String = clauses.joinToString("\n\n", transform = Clause::toMacroString)

    companion object {

        fun from(term: OtpErlangObject, attributes: Attributes): Function? =
                ifTag(term, TAG) { Function(it, attributes) }

    }
}

private val DECOMPILERS = arrayOf(Unquoted.INSTANCE, Default.INSTANCE)

private fun String.callDefinitionNameDecompiler() =
        DECOMPILERS.find {
            it.accept(MacroNameArity(Visibility.PUBLIC, this, 0))
        }

private fun OtpErlangAtom?.decompileAsCallDefinitionName() =
        this?.let { compiled ->
            compiled.atomValue().let { exportName ->
                exportName.callDefinitionNameDecompiler().toName(exportName)
            }
        }

private fun org.elixir_lang.beam.decompiler.MacroNameArity?.toName(exportName: String): String =
        if (this != null) {
            val decompiled = StringBuilder()

            this.appendName(decompiled, exportName)

            decompiled.toString()
        } else {
            exportName
        }
