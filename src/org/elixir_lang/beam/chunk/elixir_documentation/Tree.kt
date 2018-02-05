package org.elixir_lang.beam.chunk.elixir_documentation

import org.elixir_lang.beam.chunk.ElixirDocumentation
import javax.swing.JTree
import javax.swing.tree.TreeModel
import javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION

class Tree(private val moduleName: String?, model: TreeModel): JTree(model) {
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
            is CallbackDoc -> {
                val nameArity = value.nameArity

                "${nameArity.name}/${nameArity.arity}"
            }
            is CallbackDocs -> "Callbacks"
            is Doc -> {
                val nameArity = value.nameArity

                "${nameArity.name}/${nameArity.arity}"
            }
            is Docs -> "Functions/Macros"
            is ElixirDocumentation -> moduleName ?: "?"
            is ModuleDoc -> "Module"
            is TypeDoc -> {
                val nameArity = value.nameArity

                "${nameArity.name}/${nameArity.arity}"
            }
            is TypeDocs -> "Types"
            else -> super.convertValueToText(value, selected, expanded, leaf, row, hasFocus)
        }
}
