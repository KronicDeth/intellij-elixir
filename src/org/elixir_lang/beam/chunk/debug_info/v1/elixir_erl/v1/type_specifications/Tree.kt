package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.type_specifications

import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1
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
            is V1 -> value.inspectedModule ?: "?"
            is Types -> "@type"
            is Type -> "${value.name}/${value.arity}"
            is Opaques -> "@opaque"
            is Opaque -> "${value.name}/${value.arity}"
            is Callbacks -> "@callback"
            is Callback -> "${value.function}/${value.arity}"
            is OptionalCallbacks -> "@optional_callbacks"
            is OptionalCallback -> "${value.name}/${value.arity}"
            is Specifications -> "@spec"
            is Specification -> "${value.module?.let { "$it." } ?: ""}${value.function}/${value.arity}"
            else -> super.convertValueToText(value, selected, expanded, leaf, row, hasFocus)
        }
}
