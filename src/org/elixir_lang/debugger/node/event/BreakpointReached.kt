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

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangPid
import com.ericsson.otp.erlang.OtpErlangTuple
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.beam.term.inspect
import org.elixir_lang.debugger.Node
import org.elixir_lang.debugger.node.Event
import org.elixir_lang.debugger.node.ProcessSnapshot

class BreakpointReached(private val pid: OtpErlangPid, private val processSnapshotList: List<ProcessSnapshot>) : Event() {
    override fun process(node: Node, eventListener: Listener) {
        node.processSuspended(pid)
        eventListener.breakpointReached(pid, processSnapshotList)
    }

    companion object {
        private const val EXPECTED_ARITY = 3
        const val NAME = "breakpoint_reached"

        private val LOGGER = Logger.getInstance(BreakpointReached::class.java)

        fun from(tuple: OtpErlangTuple): Event? {
            val arity = tuple.arity()

            return if (arity == EXPECTED_ARITY) {
                val pid = tuple.elementAt(1)

                if (pid is OtpErlangPid) {
                    val otpSnapshotWithStacks = tuple.elementAt(2)

                    if (otpSnapshotWithStacks is OtpErlangList) {
                        otpSnapshotWithStacksToProcessSnapshotList(otpSnapshotWithStacks)?.let { processSnapshotList ->
                            BreakpointReached(pid, processSnapshotList)
                        }
                    } else {
                        LOGGER.error("Element at index 2 (${inspect(otpSnapshotWithStacks)}) is not an OtpErlangList")

                        null
                    }
                } else {
                    LOGGER.error("Element at index 1 (${inspect(pid)}) is not an OtpErlangPid")

                    null
                }
            } else {
                LOGGER.error(
                        "Tuple arity ($arity) differs from expected arity ($EXPECTED_ARITY) in `:breakpoint_reached` " +
                                "message (`${inspect(tuple)}`)"
                )

                null
            }
        }

        private fun otpSnapshotWithStacksToProcessSnapshotList(
                otpSnapshotWithStacks: OtpErlangList
        ): List<ProcessSnapshot> =
                otpSnapshotWithStacks.withIndex().mapNotNull { indexedTerm->
                    ProcessSnapshot.from(indexedTerm)
                }
    }
}
