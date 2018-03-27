package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.AbstractCodeCompileOptions
import javax.swing.event.EventListenerList
import javax.swing.event.TreeModelListener
import javax.swing.tree.TreeModel
import javax.swing.tree.TreePath

class Model(private val debugInfo: AbstractCodeCompileOptions): TreeModel {
    private val listenerList = EventListenerList()

    override fun addTreeModelListener(listener: TreeModelListener?) {
        listenerList.add(TreeModelListener::class.java, listener)
    }

    override fun getChild(parent: Any?, index: Int): Any? =
            when (parent) {
                is AbstractCodeCompileOptions -> {
                    when (index) {
                        0 -> parent.attributes
                        1 -> parent.functions
                        else -> null
                    }
                }
                is Attributes -> parent.macroStringAttributes[index]
                is Function -> parent.clauses[index]
                is Functions -> parent.functions[index]
                else -> null
            }

    override fun getChildCount(parent: Any?): Int =
            when (parent) {
                is AbstractCodeCompileOptions -> 2
                is Attributes -> parent.macroStringAttributes.size
                is Function -> parent.clauses.size
                is Functions -> parent.functions.size
                else -> 0
            }

    override fun getIndexOfChild(parent: Any?, child: Any?): Int =
        when (parent) {
            is AbstractCodeCompileOptions ->
                    when (child) {
                        is Attributes -> 0
                        is Functions -> 1
                        else -> throw IllegalArgumentException("Erlang AbstractCode has only Attributes and Functions")
                    }
            is Attributes -> parent.macroStringAttributes.indexOf(child)
            is Function -> parent.clauses.indexOf(child)
            is Functions -> parent.functions.indexOf(child)
            else -> TODO()
        }

    override fun getRoot(): AbstractCodeCompileOptions = debugInfo

    override fun isLeaf(node: Any?): Boolean =
            when (node) {
                is AbstractCodeCompileOptions -> node.abstractCode == null
                is Attributes -> node.macroStringAttributes.isEmpty()
                is Function -> node.clauses.isEmpty()
                is Functions -> node.functions.isEmpty()
                else -> true
            }

    override fun removeTreeModelListener(listener: TreeModelListener?) {
        listenerList.remove(TreeModelListener::class.java, listener)
    }

    override fun valueForPathChanged(path: TreePath?, newValue: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
