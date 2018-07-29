package org.elixir_lang.debugger.stack_frame.value

import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.icons.AllIcons
import com.intellij.xdebugger.frame.XValue
import com.intellij.xdebugger.frame.XValueChildrenList
import javax.swing.Icon

abstract class Indexed<out T : OtpErlangObject>(value: T, childCount: Int) : LazyParent<T>(value, childCount) {
    override val icon: Icon = AllIcons.Debugger.Db_array
}

fun XValueChildrenList.add(index: Int, numericChild: Long) {
    add(index, OtpErlangLong(numericChild))
}

fun XValueChildrenList.add(index: Int, child: OtpErlangObject) {
    add(index, Factory.create(child))
}

fun XValueChildrenList.add(index: Int, child: XValue) {
    add("[$index]", child)
}

