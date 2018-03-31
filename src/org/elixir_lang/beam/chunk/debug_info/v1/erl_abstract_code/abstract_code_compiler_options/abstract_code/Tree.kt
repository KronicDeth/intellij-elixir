package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.AbstractCodeCompileOptions
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.function.Clause
import javax.swing.JTree
import javax.swing.tree.TreeModel
import javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION

class Tree(model: TreeModel): JTree(model) {
    init {
        selectionModel.selectionMode = SINGLE_TREE_SELECTION
    }

    override fun convertValueToText(
            value: Any?,
            selected: Boolean,
            expanded: Boolean,
            leaf: Boolean,
            row: Int,
            hasFocus: Boolean
    ): String =
        when (value) {
            is AbstractCodeCompileOptions -> value.inspectedModule ?: "?"
            is Attribute -> value.name ?: "?"
            is Attributes -> "Attributes"
            is Clause -> value.head
            is Function -> "${value.name}/${value.arity}"
            is Functions -> "Functions"
            else -> super.convertValueToText(value, selected, expanded, leaf, row, hasFocus)
        }
}
