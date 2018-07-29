package org.elixir_lang.debugger.stack_frame.variable.elixir

import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.xdebugger.frame.XValueChildrenList
import com.intellij.xdebugger.frame.XValueGroup
import org.elixir_lang.debugger.stack_frame.value.LazyContainer
import org.elixir_lang.debugger.stack_frame.value.computeChildren
import org.elixir_lang.debugger.stack_frame.variable.Erlang

class PreviousBindingGroup(private val previousBindings: List<Erlang<OtpErlangObject>>) :
        LazyContainer, XValueGroup("Previous Bindings") {
    override val childCount: Int by lazy { previousBindings.size }
    override var nextChildIndexToCompute = 0

    override fun computeChildren(node: XCompositeNode) {
        computeChildren(this, node)
    }

    override fun computeChild(children: XValueChildrenList, index: Int) {
        children.add(previousBindings[index])
    }
}
