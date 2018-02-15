/*
 * Copyright 2012-2014 Sergey Ignatov
 * Copyright 2017 Luke Imhoff
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.elixir_lang.debugger.node.event

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangPid
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.debugger.Node
import org.elixir_lang.debugger.node.Event
import org.elixir_lang.debugger.node.ProcessSnapshot
import org.elixir_lang.debugger.node.TraceElement
import org.elixir_lang.debugger.node.VariableBinding
import org.elixir_lang.debugger.node.event.OtpErlangTermUtil.*

class BreakpointReached @Throws(FormatException::class)
constructor(breakpointReachedMessage: OtpErlangTuple) : Event() {
    init {
        assert(breakpointReachedMessage.arity() == 3)
    }

    private val pid: OtpErlangPid = breakpointReachedMessage.elementAt(1) as OtpErlangPid
    private val processSnapshotList: List<ProcessSnapshot> =
            (breakpointReachedMessage.elementAt(2) as OtpErlangList).map { element ->
                val elementTuple = element as OtpErlangTuple

                // {Pid, Function, Status, Info, Stack}
                assert(elementTuple.arity() == 5)

                val pid = elementTuple.elementAt(0) as OtpErlangPid
                val stack = otpStackToDebuggerStack(elementTuple.elementAt(4) as OtpErlangList)

                ProcessSnapshot(pid, stack)
            }

    override fun process(node: Node, eventListener: Listener) {
        node.processSuspended(pid)
        eventListener.breakpointReached(pid, processSnapshotList)
    }

    companion object {
        const val NAME = "breakpoint_reached"

        private fun otpBindingsToDebuggerBindings(otpBindings: OtpErlangList?): Collection<VariableBinding> =
            otpBindings?.map { otpBindingObject ->
                val otpBindingTuple = otpBindingObject as OtpErlangTuple

                assert(otpBindingTuple.arity() == 2)

                val variableName = (otpBindingTuple.elementAt(0) as OtpErlangAtom).atomValue()!!
                val variableValue = otpBindingTuple.elementAt(1)!!

                VariableBinding(variableName, variableValue)
            }.orEmpty()

        private fun otpStackToDebuggerStack(otpStack: OtpErlangList): List<TraceElement> =
            otpStack.map { otpStackElementObject ->
                val otpStackElementTuple = otpStackElementObject as OtpErlangTuple

                assert(otpStackElementTuple.arity() == 4)

                val moduleFunctionArgumentsTuple = otpStackElementTuple.elementAt(1) as OtpErlangTuple
                val bindingsList = getListValue(otpStackElementTuple.elementAt(2))
                val fileLineTuple = otpStackElementTuple.elementAt(3) as OtpErlangTuple

                assert(fileLineTuple.arity() == 2)

                val file = getStringText(fileLineTuple.elementAt(0))!!
                val line = getIntegerValue(fileLineTuple.elementAt( 1))!!

                traceElement(moduleFunctionArgumentsTuple, bindingsList, file, line)
            }

        private fun traceElement(moduleFunctionArgsTuple: OtpErlangTuple,
                                 bindingsList: OtpErlangList?,
                                 file: String?,
                                 line: Int?): TraceElement {
            assert(moduleFunctionArgsTuple.arity() == 3)

            val moduleName = (moduleFunctionArgsTuple.elementAt( 0) as OtpErlangAtom).atomValue()!!
            val functionName = (moduleFunctionArgsTuple.elementAt( 1) as OtpErlangAtom).atomValue()!!
            val args = getListValue(moduleFunctionArgsTuple.elementAt( 2))!!
            val bindings = otpBindingsToDebuggerBindings(bindingsList)

            return TraceElement(moduleName, functionName, args, bindings, file, line!!)
        }
    }
}
