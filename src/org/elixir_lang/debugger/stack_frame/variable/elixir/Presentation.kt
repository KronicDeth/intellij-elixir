package org.elixir_lang.debugger.stack_frame.variable.elixir

import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.xdebugger.frame.XValue
import com.intellij.xdebugger.frame.presentation.XValuePresentation
import org.elixir_lang.debugger.stack_frame.value.Factory
import org.elixir_lang.debugger.stack_frame.value.LazyParent
import org.elixir_lang.debugger.stack_frame.variable.Erlang

class Presentation<T : OtpErlangObject>(private val erlangVariableList: List<Erlang<T>>) : XValuePresentation() {
    override fun renderValue(renderer: XValueTextRenderer) {
        if (erlangVariableList.size > 1) {
            renderer.renderComment("last (#${erlangVariableList.size}) binding")
            renderer.renderSpecialSymbol(" ")
        }

        erlangVariableList
                .last()
                .term
                .let { Factory.create(it) }
                .presentation
                .renderValue(renderer)
    }
}

private val XValue.presentation: XValuePresentation
    get() =
        if (this is LazyParent<*>) {
            this.presentation
        } else {
            TODO()
        }
