package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.type_specifications

import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1
import javax.swing.event.EventListenerList
import javax.swing.event.TreeModelListener
import javax.swing.tree.TreeModel
import javax.swing.tree.TreePath

class Model(private val debugInfo: V1): TreeModel {
    private val listenerList = EventListenerList()

    override fun addTreeModelListener(listener: TreeModelListener?) {
        listenerList.add(TreeModelListener::class.java, listener)
    }

    override fun getChild(parent: Any?, index: Int): Any? =
        when (parent) {
            is V1 ->
                when (index) {
                    0 -> parent.typeSpecifications?.types
                    1 -> parent.typeSpecifications?.callbacks
                    2 -> parent.typeSpecifications?.optionalCallbacks
                    3 -> parent.typeSpecifications?.specifications
                    else -> null
                }
            is Types -> parent[index]
            is Callbacks -> parent[index]
            is OptionalCallbacks -> parent[index]
            is Specifications -> parent[index]
            else -> null
        }

    override fun getChildCount(parent: Any?): Int =
            when (parent) {
                is V1 -> 3
                is Types -> parent.size()
                is Callbacks -> parent.size()
                is OptionalCallbacks -> parent.size()
                is Specifications -> parent.size()
                else -> 0
            }

    override fun getIndexOfChild(parent: Any?, child: Any?): Int =
        when (parent) {
            is V1 ->
                when (child) {
                    is Types -> 0
                    is Callbacks -> 1
                    is OptionalCallbacks -> 2
                    is Specifications -> 3
                    else -> 0
                }
            is Types -> parent.indexOf(child as Type)
            is Callbacks -> parent.indexOf(child as Callback)
            is OptionalCallbacks -> parent.indexOf(child as OptionalCallback)
            is Specifications -> parent.indexOf(child as Specification)
            else -> 0
        }

    override fun getRoot(): V1 = debugInfo

    override fun isLeaf(node: Any?): Boolean =
        when (node) {
            is V1 -> false
            is Types -> node.size() == 0
            is Callbacks -> node.size() == 0
            is OptionalCallbacks -> node.size() == 0
            is Specifications -> node.size() == 0
            else -> true
        }

    override fun removeTreeModelListener(listener: TreeModelListener?) {
        listenerList.remove(TreeModelListener::class.java, listener)
    }

    override fun valueForPathChanged(path: TreePath?, newValue: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
