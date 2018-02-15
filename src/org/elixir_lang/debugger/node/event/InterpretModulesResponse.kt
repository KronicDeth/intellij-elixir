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
import com.intellij.openapi.vfs.LocalFileSystem
import org.elixir_lang.debugger.Node
import org.elixir_lang.debugger.node.Event
import org.elixir_lang.debugger.stack_frame.value.Presentation.toUtf8String

class InterpretModulesResponse(receivedMessage: OtpErlangTuple) : Event() {
    private val nodeName: String = (receivedMessage.elementAt(1) as OtpErlangAtom).atomValue()
    private val errorReasonByModule = mutableMapOf<String, OtpErlangObject>()

    init {
        OtpErlangTermUtil.getListValue(receivedMessage.elementAt(2))!!.forEach { statusObject ->
            val statusTuple = statusObject as OtpErlangTuple

            assert(statusTuple.arity() == 2)

            val moduleStatusObject = statusTuple.elementAt( 1)!!

            when (moduleStatusObject) {
                is OtpErlangTuple -> {
                    assert(moduleStatusObject.arity() == 2)

                    val tag = moduleStatusObject.elementAt(0)!!
                    assert(tag is OtpErlangAtom && tag.atomValue() == "error")

                    val module: String = module(statusTuple.elementAt(0))

                    errorReasonByModule[module] = moduleStatusObject.elementAt(1)!!
                }
                else -> assert(moduleStatusObject is OtpErlangAtom && moduleStatusObject.atomValue() == "ok")
            }
        }
    }

    override fun process(node: Node, eventListener: Listener) {
        if (!errorReasonByModule.isEmpty()) {
            eventListener.failedToInterpretModules(nodeName, errorReasonByModule)
        }
    }

    companion object {
        const val NAME = "interpret_modules_response"

        private fun module(moduleTerm: OtpErlangObject): String =
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
                        }!!
                    }
                }
    }
}
