/*
 * Copyright 2012-2014 Sergey Ignatov
 * Copyright 2017 Jake Becker
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

import com.intellij.xdebugger.frame.XExecutionStack
import com.intellij.xdebugger.frame.XStackFrame
import org.elixir_lang.debugger.node.ProcessSnapshot

internal class ExecutionStack(private val process: Process, private val processSnapshot: ProcessSnapshot) :
        XExecutionStack(processSnapshot.pidString) {
    private val stackFrames: List<StackFrame> by lazy {
        processSnapshot.stack.map { traceElement ->
           StackFrame(process, processSnapshot.pid, traceElement)
       }
    }

    override fun getTopFrame(): XStackFrame? = stackFrames.firstOrNull()

    override fun computeStackFrames(firstFrameIndex: Int, container: XExecutionStack.XStackFrameContainer) {
        container.addStackFrames(stackFrames, true)
    }
}
