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
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.beam.term.inspect
import org.elixir_lang.debugger.InterpretedModule
import org.elixir_lang.debugger.Node
import org.elixir_lang.debugger.node.Event

class Interpreted(private val interpretedModuleList: List<InterpretedModule>) : Event() {
    override fun process(node: Node, eventListener: Listener) {
        eventListener.interpretedModules(interpretedModuleList)
    }

    companion object {
        const val NAME = "interpreted"

        private val LOGGER = Logger.getInstance(Interpreted::class.java)

        fun from(tuple: OtpErlangTuple): Event? {
            val arity = tuple.arity()

            return if (arity == EXPECTED_ARITY) {
                val interpretedModuleList = tuple.elementAt(1)

                if (interpretedModuleList is OtpErlangList) {
                    from(interpretedModuleList)
                } else {
                    LOGGER.error("Element at index 1 (${inspect(interpretedModuleList)}) is not an OtpErlangList")

                    null
                }
            } else {
                LOGGER.error(
                        "Tuple arity ($arity) differs from expected arity ($EXPECTED_ARITY) in `:interpreted` " +
                                "message (`${inspect(tuple)}`)"
                )

                null
            }
        }

        private const val EXPECTED_ARITY = 2
        private const val EXPECTED_INTERPRETED_MODULE_ARITY = 2
        private const val INTERPRETED_INDEX = 0
        private const val MODULE_INDEX = 1

        private fun from(list: OtpErlangList): Event =
            list
                    .withIndex()
                    .mapNotNull { (index, element) -> interpretedModuleFrom(index, element) }
                    .let { Interpreted(it) }

        private fun interpretedModuleFrom(index: Int, element: OtpErlangObject): InterpretedModule? =
            when (element) {
                is OtpErlangTuple -> interpretedModuleFrom(index, element)
                else -> {
                    LOGGER.error("Index ($index) is not a tuple in list at index 1 in `:interpreted` message")

                    null
                }
            }

        private fun interpretedModuleFrom(index: Int, tuple: OtpErlangTuple): InterpretedModule? {
            val arity = tuple.arity()

            return if (arity == EXPECTED_INTERPRETED_MODULE_ARITY) {
                val interpretedTerm = tuple.elementAt(INTERPRETED_INDEX)

                if (interpretedTerm is OtpErlangAtom) {
                    val interpreted = interpretedTerm.booleanValue()
                    val moduleTerm = tuple.elementAt(MODULE_INDEX)

                    if (moduleTerm is OtpErlangAtom) {
                        InterpretedModule(interpreted, moduleTerm)
                    } else {
                        LOGGER.error("Index ($index) tuple index ($MODULE_INDEX) element (${inspect(moduleTerm)}) is not a module")

                        null
                    }
                } else {
                    LOGGER.error("Index ($index) tuple index ($INTERPRETED_INDEX) element (${inspect(interpretedTerm)}) is not an atom")

                    null
                }
            } else {
                LOGGER.error("Index ($index) tuple arity ($arity) differs from expected arity ($EXPECTED_INTERPRETED_MODULE_ARITY)")

                null
            }
        }

    }
}
