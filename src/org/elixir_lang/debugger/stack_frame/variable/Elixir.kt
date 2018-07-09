package org.elixir_lang.debugger.stack_frame.variable

import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.xdebugger.frame.*
import org.elixir_lang.debugger.stack_frame.variable.elixir.PreviousBindingGroup

class Elixir(name: String, private val erlangVariableList: List<Erlang<OtpErlangObject>>) : XNamedValue(name) {
    val currentBinding by lazy { erlangVariableList.last() }

    override fun computeChildren(node: XCompositeNode) {
        if (hasChildren) {
            val children = XValueChildrenList()

            val previousBindingGroup = PreviousBindingGroup(previousBindings)
            children.addTopGroup(previousBindingGroup)

            children.add(currentBinding)

            node.addChildren(children, true)
        } else if (currentBinding.hasChildren) {
            currentBinding.computeChildren(node)
        }
    }

    override fun computePresentation(node: XValueNode, place: XValuePlace) {
        if (hasChildren) {
            node.setPresentation(currentBinding.icon, currentBinding.xValuePresentation, hasChildren)
        } else {
            node.setPresentation(currentBinding.icon, currentBinding.xValuePresentation, currentBinding.hasChildren)
        }
    }

    private val previousBindings by lazy { erlangVariableList.dropLast(1) }
    private val hasChildren by lazy { previousBindings.isNotEmpty() }
}
