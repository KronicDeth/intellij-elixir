/*
 * Copyright 2012-2014 Sergey Ignatov
 * Copyright 2017-2018 Luke Imhoff
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
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.debugger.Node
import org.elixir_lang.debugger.node.Event

class SetBreakpointResponse(message: OtpErlangTuple) : Event() {
    init {
        assert(message.arity() == 5)
    }

    private val module: String = messageToModule(message)
    private val line: Int = messageToLine(message)
    private val errorReason: OtpErlangObject? = messageToErrorReason(message)
    private val file: String = messageToFile(message)

    override fun process(node: Node, eventListener: Listener) {
        if (errorReason == null) {
            eventListener.breakpointIsSet(module, file, line)
        } else {
            eventListener.failedToSetBreakpoint(module, file, line, errorReason)
        }
    }

    companion object {
        const val NAME = "set_breakpoint_response"

        private fun messageToErrorReason(message: OtpErlangTuple) =
            statusToErrorReason(message.elementAt(3))

        private fun messageToFile(message: OtpErlangTuple) =
                OtpErlangTermUtil.getStringText(message.elementAt(4))!!

        private fun messageToLine(message: OtpErlangTuple) =
                OtpErlangTermUtil.getIntegerValue(message.elementAt(2))!! - 1

        private fun messageToModule(message: OtpErlangTuple) =
                (message.elementAt(1) as OtpErlangAtom).atomValue()

        private fun statusToErrorReason(status: OtpErlangObject) =
            if (status is OtpErlangTuple) {
                statusToErrorReason(status)
            } else {
                assert(status is OtpErlangAtom && status.atomValue() == "ok")

                null
            }

        private fun statusToErrorReason(status: OtpErlangTuple): OtpErlangObject {
            assert(status.arity() == 2)

            val tag = status.elementAt(0)!!
            assert(tag is OtpErlangAtom && tag.atomValue() == "error")

            return status.elementAt(1)!!
        }
    }
}
