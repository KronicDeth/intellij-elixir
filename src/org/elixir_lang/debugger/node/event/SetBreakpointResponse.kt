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

import com.ericsson.otp.erlang.*
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.beam.term.inspect
import org.elixir_lang.debugger.Node
import org.elixir_lang.debugger.node.ErrorReason
import org.elixir_lang.debugger.node.Event
import org.elixir_lang.debugger.node.OK
import org.elixir_lang.debugger.node.OKErrorReason
import org.elixir_lang.debugger.stack_frame.value.Presentation.toUtf8String

class SetBreakpointResponse(
        private val module: String,
        private val line: Int,
        private val okErrorReason: OKErrorReason,
        private val file: String
) : Event() {
    override fun process(node: Node, eventListener: Listener) =
        when (okErrorReason) {
            OK -> eventListener.breakpointIsSet(module, file, line)
            is ErrorReason -> eventListener.failedToSetBreakpoint(module, file, line, okErrorReason.reason)
        }

    companion object {
        // {:set_breakpoint_response, module, line, :ok | {:error, :break_exists}, file}
        private const val ARITY = 5
        const val NAME = "set_breakpoint_response"

        private val LOGGER by lazy { Logger.getInstance(SetBreakpointResponse::class.java) }

        fun from(tuple: OtpErlangTuple): SetBreakpointResponse? {
            val arity = tuple.arity()

            return if (arity == ARITY) {
                module(tuple.elementAt(1))?.let { module ->
                    line(tuple.elementAt(2))?.let { line ->
                        OKErrorReason.from(tuple.elementAt(3))?.let { okErrorReason ->
                            file(tuple.elementAt(4))?.let { file ->
                                SetBreakpointResponse(module, line, okErrorReason, file)
                            }
                        }
                    }
                }
            } else {
                LOGGER.error(":$NAME tuple (${inspect(tuple)}) arity ($arity) is not $ARITY")

                null
            }
        }

        // Private Functions

        private fun file(term: OtpErlangObject): String? =
                when (term) {
                    is OtpErlangBinary -> toUtf8String(term)
                    else -> {
                        LOGGER.error("file (${inspect(term)}) is not an OtpErlangBinary")

                        null
                    }
                }

        private fun line(term: OtpErlangObject): Int? =
                when (term) {
                    is OtpErlangLong -> term.intValue()
                    else -> {
                        LOGGER.error("line (${inspect(term)}) is not an OtpErlangLong")

                        null
                    }
                }

        private fun module(term: OtpErlangObject): String? =
                when (term) {
                    is OtpErlangAtom -> term.atomValue()
                    else -> {
                        LOGGER.error("module (${inspect(term)}) is not an OtpErlangAtom")

                        null
                    }
                }
    }
}
