package org.elixir_lang.debugger.stack_frame.value.list

import com.ericsson.otp.erlang.OtpErlangList
import com.intellij.icons.AllIcons
import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.xdebugger.frame.XValueChildrenList
import org.elixir_lang.debugger.stack_frame.value.Presentable
import org.elixir_lang.debugger.stack_frame.value.add
import javax.swing.Icon

class Improper(term: OtpErlangList): Presentable<OtpErlangList>(term) {
    override val hasChildren: Boolean = true
    override val icon: Icon = AllIcons.Debugger.Db_array

    override fun computeChildren(node: XCompositeNode) {
        val children = XValueChildrenList()

        children.add("head", term.head)
        children.add("tail", term.lastTail)

        node.addChildren(children, true)
    }
}
