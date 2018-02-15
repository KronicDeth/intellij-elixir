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
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.debugger.Node
import org.elixir_lang.debugger.node.Event

class DebugRemoteNodeResponse(receivedMessage: OtpErlangTuple) : Event() {
    private val nodeName: String = (receivedMessage.elementAt(1) as OtpErlangAtom).atomValue()
    private val error: String?

    init {
        val status = (receivedMessage.elementAt(2) as OtpErlangAtom).atomValue()!!
        error = if ("ok" == status) null else status
    }

    override fun process(node: Node, eventListener: Listener) {
        error?.let { error ->
            eventListener.failedToDebugRemoteNode(nodeName, error)
        }
    }

    companion object {
        const val NAME = "debug_remote_node_response"
    }
}
