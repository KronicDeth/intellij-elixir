package org.elixir_lang.debugger.stack_frame.value.list

import com.ericsson.otp.erlang.OtpErlangList
import com.intellij.icons.AllIcons
import com.intellij.xdebugger.frame.*
import org.elixir_lang.debugger.stack_frame.value.Base.addNamedChild
import org.elixir_lang.debugger.stack_frame.value.Presentation

class Improper(private val value: OtpErlangList): XValue() {
    override fun computeChildren(node: XCompositeNode) {
        val children = XValueChildrenList()

        addNamedChild(children, value.head, "head")
        addNamedChild(children, value.lastTail, "tail")

        node.addChildren(children, true)
    }

    override fun computePresentation(node: XValueNode, place: XValuePlace) {
        val presentation = Presentation(value)

        node.setPresentation(AllIcons.Debugger.Db_array, presentation, true)
    }
}
