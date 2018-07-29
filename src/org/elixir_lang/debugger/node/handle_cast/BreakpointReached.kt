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

package org.elixir_lang.debugger.node.handle_cast

import com.ericsson.otp.erlang.*
import org.elixir_lang.Clause
import org.elixir_lang.debugger.Node
import org.elixir_lang.debugger.node.ProcessSnapshot
import org.elixir_lang.debugger.node.event.Listener

class BreakpointReached(private val node : Node, private val eventListener: Listener) : Clause {
    override fun match(arguments: OtpErlangList): Match? {
        assert(arguments.arity() == 1)

        return arguments
                .elementAt(0)
                .let { it as? OtpErlangTuple }
                ?.let { tuple ->
                    if (tuple.arity() == 3) {
                        tuple
                    } else {
                        null
                    }
                }
                ?.let { tuple ->
                    if (tuple.elementAt(0) == BREAKPOINT_REACHED && tuple.elementAt(1) is OtpErlangPid && tuple.elementAt(2) is OtpErlangList) {
                        val pid = tuple.elementAt(1) as? OtpErlangPid
                        val otpSnapshotWithStacks = tuple.elementAt(2) as? OtpErlangList

                        if (pid != null && otpSnapshotWithStacks != null) {
                            Match(pid, otpSnapshotWithStacks)
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                }
    }

    override fun run(match: org.elixir_lang.clause.Match): OtpErlangObject {
        val breakpointReachedMatch = match as Match
        val pid = breakpointReachedMatch.pid
        val processSnapshotList = breakpointReachedMatch.snapshotsWithStacks.toProcessSnapshotList()

        node.processSuspended(pid)
        eventListener.breakpointReached(pid, processSnapshotList)

        return OtpErlangAtom("ok")
    }

    inner class Match(val pid: OtpErlangPid, val snapshotsWithStacks: OtpErlangList) : org.elixir_lang.clause.Match
}

private val BREAKPOINT_REACHED = OtpErlangAtom("breakpoint_reached")

private fun OtpErlangList.toProcessSnapshotList(): List<ProcessSnapshot> =
        withIndex().mapNotNull { indexedTerm->
            ProcessSnapshot.from(indexedTerm)
        }
