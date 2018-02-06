package org.elixir_lang.beam.chunk.elixir_documentation

import org.elixir_lang.beam.chunk.ElixirDocumentation
import javax.swing.event.EventListenerList
import javax.swing.event.TreeModelListener
import javax.swing.tree.TreeModel
import javax.swing.tree.TreePath

class Model(private val elixirDocumentation: ElixirDocumentation): TreeModel {
    private val listenerList = EventListenerList()

    override fun getRoot(): ElixirDocumentation = elixirDocumentation

    override fun isLeaf(node: Any?): Boolean =
        when (node) {
            is CallbackDocs -> node.size() == 0
            is Docs -> node.size() == 0
            is TypeDocs -> node.size() == 0
            is ElixirDocumentation -> false
            else -> true
        }

    override fun getChildCount(parent: Any?): Int =
            when (parent) {
                is CallbackDocs -> parent.size()
                is Docs -> parent.size()
                is ElixirDocumentation -> 4
                is TypeDocs -> parent.size()
                else -> 0
            }

    override fun removeTreeModelListener(listener: TreeModelListener?) {
        listenerList.remove(TreeModelListener::class.java, listener)
    }

    override fun valueForPathChanged(path: TreePath?, newValue: Any?) = throw UnsupportedOperationException()

    override fun getIndexOfChild(parent: Any?, child: Any?): Int =
        when (parent) {
            is CallbackDocs -> parent[child as CallbackDoc]
            is Docs -> parent[child as Doc]
            is TypeDocs -> parent[child as TypeDoc]
            else -> 0
        }

    override fun getChild(parent: Any?, index: Int): Any? =
        when (parent) {
            is CallbackDocs -> parent[index]
            is Docs -> parent[index]
            is ElixirDocumentation ->
                    when (index) {
                        0 -> parent.moduledoc
                        1 -> parent.typeDocs
                        2 -> parent.callbackDocs
                        3 -> parent.docs
                        else -> throw IndexOutOfBoundsException()
                    }
            is TypeDocs -> parent[index]
            else -> null
        }

    override fun addTreeModelListener(listener: TreeModelListener?) {
        listenerList.add(TreeModelListener::class.java, listener)
    }
}
