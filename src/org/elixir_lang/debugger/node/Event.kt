/*
 * Copyright 2012-2014 Sergey Ignatov
 * Copyright 2018 Luke Imhoff
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

package org.elixir_lang.debugger.node

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.debugger.Node
import org.elixir_lang.debugger.node.event.*

abstract class Event {
    abstract fun process(node: Node, eventListener: Listener)

    companion object {
        fun from(message: OtpErlangObject): Event? =
            when (message) {
                is OtpErlangTuple -> from(message)
                else -> null
            }

        fun from(message: OtpErlangTuple): Event? =
            if (message.arity() > 0) {
                (message.elementAt(0) as? OtpErlangAtom)?.atomValue()?.let { messageName ->
                    when (messageName) {
                        BreakpointReached.NAME -> BreakpointReached.from(message)
                        DebugRemoteNodeResponse.NAME -> DebugRemoteNodeResponse.from(message)
                        InterpretModulesResponse.NAME -> InterpretModulesResponse.from(message)
                        SetBreakpointResponse.NAME -> SetBreakpointResponse.from(message)
                        else -> null
                    }
                }
            } else {
                null
            }
    }
}
