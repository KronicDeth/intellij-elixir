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

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.beam.term.inspect
import org.elixir_lang.debugger.Node
import org.elixir_lang.debugger.node.Event

class Evaluated(val result: OtpErlangObject) : Event() {
    override fun process(node: Node, eventListener: Listener) =
            node.evaluated(result)

    companion object {
        // {:evaluated, result}
        const val ARITY = 2
        const val NAME = "evaluated"

        private val LOGGER = Logger.getInstance(Evaluated::class.java)

        fun from(tuple: OtpErlangTuple): Evaluated? {
            val arity = tuple.arity()

            return if (arity == ARITY) {
                Evaluated(tuple.elementAt(1))
            } else {
                LOGGER.error(":$NAME message (${inspect(tuple)}) arity ($arity) is not $ARITY")

                null
            }
        }
    }
}
