package org.elixir_lang.structure_view.element.call_definition_by_name_arity

import com.intellij.ide.util.treeView.smartTree.TreeElement
import org.elixir_lang.ArityRange
import org.elixir_lang.Name
import org.elixir_lang.NameArity
import org.elixir_lang.psi.call.Call
import org.elixir_lang.structure_view.element.CallDefinition
import org.elixir_lang.structure_view.element.Timed
import org.elixir_lang.structure_view.element.modular.Modular
import java.util.*

/**
 * A [CallDefinitionByNameArity] that inserts the [org.elixir_lang.structure_view.element.CallDefinition] it
 * generates into a `List<TreeElement>`.
 */
open class TreeElementList(
        size: Int,
        protected val treeElementList: MutableList<TreeElement>,
        protected val modular: Modular,
        private val time: Timed.Time
) : HashMap<NameArity, CallDefinition>(size), CallDefinitionByNameArity {
    fun addClausesToCallDefinition(call: Call) {
        org.elixir_lang.psi.CallDefinitionClause.nameArityRange(call)?.let { (name, arityRange) ->
            addClausesToCallDefinition(call, name, arityRange)
        }
    }

    private fun addClausesToCallDefinition(call: Call, name: Name, arityRange: ArityRange) {
        for (arity in arityRange) {
            NameArity(name, arity).let { putNew(it) }.clause(call)
        }
    }

    open fun addToTreeElementList(callDefinition: CallDefinition) {
        treeElementList.add(callDefinition)
    }

    /**
     * Generates a [CallDefinition] for the given `nameArity` if it does not exist.
     *
     *
     * The [CallDefinition] is
     *
     * @param nameArity
     * @return pre-existing [CallDefinition] or new [CallDefinition] add to the `List<TreeElement>`
     */
    override fun putNew(nameArity: NameArity): CallDefinition =
    // Don't use `computeIfAbsent` because `addToTreeElementList` needs to be called only when absent, but after
            // `put`, which would be after `computeIfAbsent` computer returned.
            get(nameArity) ?: CallDefinition(
                    modular,
                    time,
                    nameArity.name,
                    nameArity.arity
            ).also {
                put(nameArity, it)
                addToTreeElementList(it)
            }
}

private fun CallDefinition.also(block: (CallDefinition) -> Unit): CallDefinition {
    block(this)
    return this
}
