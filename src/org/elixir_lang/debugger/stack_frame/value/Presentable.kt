package org.elixir_lang.debugger.stack_frame.value

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.icons.AllIcons
import com.intellij.xdebugger.frame.XValue
import com.intellij.xdebugger.frame.XValueChildrenList
import com.intellij.xdebugger.frame.XValueNode
import com.intellij.xdebugger.frame.XValuePlace
import com.intellij.xdebugger.frame.presentation.XValuePresentation
import javax.swing.Icon

abstract class Presentable<out T : OtpErlangObject>(protected val term: T) : XValue() {
    abstract val hasChildren: Boolean
    open val icon: Icon = AllIcons.Debugger.Value
    open val xValuePresentation: XValuePresentation by lazy {
        Presentation(term)
    }

    override fun canNavigateToSource(): Boolean = false

    override fun computePresentation(node: XValueNode, place: XValuePlace) {
        node.setPresentation(icon, xValuePresentation, hasChildren)
    }
}

fun XValueChildrenList.add(name: kotlin.String, child: OtpErlangObject) {
    add(name, Factory.create(child))
}

fun XValueChildrenList.add(name: kotlin.String, atomString: kotlin.String) {
    add(name, OtpErlangAtom(atomString))
}

fun XValueChildrenList.add(name: kotlin.String, int: Int) {
    add(name, OtpErlangLong(int.toLong()))
}
