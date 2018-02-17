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
import com.intellij.openapi.vfs.LocalFileSystem
import org.elixir_lang.beam.term.inspect
import org.elixir_lang.debugger.Node
import org.elixir_lang.debugger.node.ErrorReason
import org.elixir_lang.debugger.node.Event
import org.elixir_lang.debugger.node.OKErrorReason
import org.elixir_lang.debugger.stack_frame.value.Presentation.toUtf8String

class InterpretModulesResponse(
        private val node: String,
        private val errorReasonByModule: Map<String, OtpErlangObject>
) : Event() {
    override fun process(node: Node, eventListener: Listener) {
        if (!errorReasonByModule.isEmpty()) {
            eventListener.failedToInterpretModules(this.node, errorReasonByModule)
        }
    }

    companion object {
        // {node, [{module, :ok | {:error, reason}}]}
        const val ARITY = 2
        private const val MODULE_OK_ERROR_REASON_ARITY = 2
        const val NAME = "interpret_modules_response"

        val LOGGER by lazy { Logger.getInstance(InterpretModulesResponse::class.java) }

        fun from(tuple: OtpErlangTuple): InterpretModulesResponse? {
            val arity = tuple.arity()

            return if (arity == ARITY) {
                node(tuple)?.let { node ->
                    errorReasonByModule(tuple)?.let { errorReasonByModule ->
                        InterpretModulesResponse(node, errorReasonByModule)
                    }
                }
            } else {
                LOGGER.error(":$NAME message (${inspect(tuple)}) arity ($arity) is not $ARITY")

                null
            }
        }

        // Private Functions

        private fun errorReasonByModule(list: OtpErlangList): Map<String, OtpErlangObject> =
            list.mapNotNull { moduleErrorReason(it) }.associate { it }

        private fun errorReasonByModule(tuple: OtpErlangTuple): Map<String, OtpErlangObject>? {
            val okErrorReasonByModule = tuple.elementAt(1)

            return when (okErrorReasonByModule) {
                is OtpErlangList -> errorReasonByModule(okErrorReasonByModule)
                else -> {
                    LOGGER.error("ErrorReasonByModule (${inspect(okErrorReasonByModule)}) is not an OtpErlangList")

                    null
                }
            }
        }

        private fun module(moduleTerm: OtpErlangObject): String? =
                when (moduleTerm) {
                    is OtpErlangAtom -> moduleTerm.atomValue()
                    else -> {
                        when (moduleTerm) {
                            is OtpErlangBinary -> toUtf8String(moduleTerm)
                            is OtpErlangList -> moduleTerm.stringValue()
                            is OtpErlangString -> moduleTerm.stringValue()
                            else -> null
                        }?.let { modulePath ->
                            LocalFileSystem.getInstance().findFileByPath(modulePath)?.nameWithoutExtension
                        }
                    }
                }

        private fun moduleErrorReason(term: OtpErlangObject): Pair<String, OtpErlangObject>? =
            when (term) {
                is OtpErlangTuple -> moduleErrorReason(term)
                else -> {
                    LOGGER.error("ModuleErrorReason (${inspect(term)}) is not an OtpErlangTuple")

                    null
                }
            }

        private fun moduleErrorReason(tuple: OtpErlangTuple): Pair<String, OtpErlangObject>? {
            val arity = tuple.arity()

            return if (arity == MODULE_OK_ERROR_REASON_ARITY) {
                module(tuple.elementAt(0))?.let { module ->
                    OKErrorReason.from(tuple.elementAt(1))?.let { okErrorReason ->
                        when (okErrorReason) {
                            is ErrorReason -> Pair(module, okErrorReason.reason)
                            else -> null
                        }
                    }
                }
            } else {
                LOGGER.error(
                        "moduleOkErrorReason (${inspect(tuple)}) arity ($arity) is not $MODULE_OK_ERROR_REASON_ARITY"
                )

                null
            }
        }

        private fun node(tuple: OtpErlangTuple): String? {
            val node = tuple.elementAt(0)

            return when (node) {
                is OtpErlangAtom -> node.atomValue()
                else -> {
                    LOGGER.error("Node (${inspect(node)}) is not an OtpErlangAtom")

                    null
                }
            }
        }
    }
}
