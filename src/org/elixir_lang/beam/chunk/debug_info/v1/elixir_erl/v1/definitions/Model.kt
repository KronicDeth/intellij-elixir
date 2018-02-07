package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions

import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.definition.Clause
import javax.swing.event.EventListenerList
import javax.swing.event.TreeModelListener
import javax.swing.tree.TreeModel
import javax.swing.tree.TreePath

class Model(private val debugInfo: V1): TreeModel {
    private val listenerList = EventListenerList()

    override fun getRoot(): V1 = debugInfo

    override fun isLeaf(node: Any?): Boolean =
        when (node) {
            is V1 -> node.definitions == null
            is Definition -> node.clauses == null
            else -> true
        }

    override fun getChildCount(parent: Any?): Int =
            when (parent) {
                is V1 -> parent.definitions?.size() ?: 0
                is Definition -> parent.clauses?.size ?: 0
                else -> 0
            }

    override fun removeTreeModelListener(listener: TreeModelListener?) {
        listenerList.remove(TreeModelListener::class.java, listener)
    }

    override fun valueForPathChanged(path: TreePath?, newValue: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getIndexOfChild(parent: Any?, child: Any?): Int =
        when (parent) {
            is V1 -> parent.definitions?.indexOf(child as Definition) ?: 0
            is Definition -> parent.clauses?.indexOf(child as Clause) ?: 0
            else -> 0
        }

    override fun getChild(parent: Any?, index: Int): Any? =
        when (parent) {
            is V1 -> parent.definitions?.get(index)
            is Definition -> parent.clauses?.get(index)
            else -> null
        }

    override fun addTreeModelListener(listener: TreeModelListener?) {
        listenerList.add(TreeModelListener::class.java, listener)
    }
}
