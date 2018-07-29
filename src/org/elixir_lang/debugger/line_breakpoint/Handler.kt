/*
 * Copyright 2012-2014 Sergey Ignatov
 * Copyright 2017 Jake Becker
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

package org.elixir_lang.debugger.line_breakpoint

import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import org.elixir_lang.debugger.Process

class Handler(private val process: Process) : XBreakpointHandler<XLineBreakpoint<Properties>>(Type::class.java) {
    override fun registerBreakpoint(breakpoint: XLineBreakpoint<Properties>) {
        process.addBreakpoint(breakpoint)
    }

    override fun unregisterBreakpoint(breakpoint: XLineBreakpoint<Properties>, temporary: Boolean) {
        process.removeBreakpoint(breakpoint)
    }
}
