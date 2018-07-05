package org.elixir_lang.debugger.stack_frame.variable

import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.xdebugger.frame.XNamedValue
import com.intellij.xdebugger.frame.XValueNode
import com.intellij.xdebugger.frame.XValuePlace
import com.intellij.xdebugger.frame.presentation.XValuePresentation
import org.elixir_lang.debugger.stack_frame.value.Factory
import org.elixir_lang.debugger.stack_frame.value.Presentable
import javax.swing.Icon

class Erlang<out T : OtpErlangObject>(name: String, val term: T) : XNamedValue(name) {
    val hasChildren: Boolean
      get() = xValue.hasChildren

    val icon: Icon?
      get() = xValue.icon

    val xValuePresentation: XValuePresentation
      get() = xValue.xValuePresentation

    override fun canNavigateToSource(): Boolean = false

    override fun computeChildren(node: XCompositeNode) {
        xValue.computeChildren(node)
    }

    override fun computePresentation(node: XValueNode, place: XValuePlace) {
        xValue.computePresentation(node, place)
    }

    private val xValue: Presentable<*> by lazy {
        Factory.create(term)
    }
}
