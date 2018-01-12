package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions

import com.ericsson.otp.erlang.OtpErlangList
import org.elixir_lang.Macro
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.definition.Clause
import javax.swing.JTree
import javax.swing.tree.TreeModel

class Tree(model: TreeModel): JTree(model) {
    override fun convertValueToText(
            value: Any?,
            selected: Boolean,
            expanded: Boolean,
            leaf: Boolean,
            row: Int,
            hasFocus: Boolean
    ): String =
        when (value) {
            is V1 -> value.module?.atomValue() ?: "?"
            is Definition -> "${value.name ?: '?'}/${value.arity ?: '?'}"
            is Clause -> "${value.definition.name}(${argumentsToText(value.arguments)})${guardsToText(value.guards)}"
            else -> super.convertValueToText(value, selected, expanded, leaf, row, hasFocus)
        }

    private fun guardsToText(guards: OtpErlangList?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun argumentsToText(arguments: OtpErlangList?): String =
            arguments?.joinToString(", ") { argument -> Macro.toString(argument) } ?: ""
}
