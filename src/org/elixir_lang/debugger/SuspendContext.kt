/*
 * Copyright 2012-2014 Sergey Ignatov
 * Copyright 2017 Jake Becker
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

package org.elixir_lang.debugger

import com.ericsson.otp.erlang.OtpErlangPid
import com.intellij.xdebugger.frame.XExecutionStack
import com.intellij.xdebugger.frame.XSuspendContext
import org.elixir_lang.debugger.node.ProcessSnapshot

internal class SuspendContext(val process: Process,
                              activePid: OtpErlangPid,
                              snapshots: List<ProcessSnapshot>) : XSuspendContext() {
    private val executionStacks: Array<XExecutionStack>
    private val activeExecutionStack: ExecutionStack?

    init {
        var activeExecutionStack: ExecutionStack? = null

        executionStacks = snapshots.map { snapshot ->
            val executionStack = ExecutionStack(process, snapshot)

            if (snapshot.pid == activePid) {
                activeExecutionStack = executionStack
            }

            executionStack
        }.toTypedArray()

        this.activeExecutionStack = activeExecutionStack
    }

    override fun getActiveExecutionStack(): XExecutionStack? = activeExecutionStack
    override fun getExecutionStacks(): Array<XExecutionStack> = executionStacks
}
