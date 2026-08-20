/*
 * Copyright 2012-2014 Sergey Ignatov
 * Copyright 2017 Jake Becker
 * Copyright 2017 Kadie Enheduanna Inanna
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

import com.intellij.execution.wsl.WSLDistribution
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.concurrency.ThreadingAssertions
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.XSourcePosition
import org.elixir_lang.debugger.node.ProcessSnapshot
import org.elixir_lang.debugger.node.TraceElement
import org.elixir_lang.sdk.wsl.wslCompat

class SourcePosition private constructor(val sourcePosition: XSourcePosition) {

    val line: Int get() = sourcePosition.line
    val file: VirtualFile get() = sourcePosition.file

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as SourcePosition
        return line == other.line && file == other.file
    }

    override fun hashCode(): Int = 31 * file.hashCode() + line

    companion object {
        private val LOGGER = Logger.getInstance(SourcePosition::class.java)

        @JvmStatic
        fun create(position: XSourcePosition): SourcePosition = SourcePosition(position)

        /**
         * Creates a [SourcePosition] from a file path and 0-based line number.
         *
         * When the Erlang VM runs on WSL, it reports Linux file paths (e.g.
         * `/home/user/project/lib/foo.ex`) from `module.module_info[:compile][:source]`.
         * [LocalFileSystem.findFileByPath] cannot resolve these on Windows.
         * If the initial lookup fails and a [WSLDistribution] is provided, the path is
         * converted to a Windows UNC path via [org.elixir_lang.sdk.wsl.WslCompatService.convertLinuxPathToWindowsUnc]
         * and retried.
         *
         * @param filePath    absolute file path as reported by the Erlang VM
         * @param line        0-based line number
         * @param distribution WSL distribution for path conversion, or null for non-WSL projects
         */
        @JvmStatic
        fun create(filePath: String, line: Int, distribution: WSLDistribution? = null): SourcePosition? {
            ThreadingAssertions.assertBackgroundThread()

            var file = LocalFileSystem.getInstance().findFileByPath(filePath)

            if (file == null && distribution != null && filePath.startsWith("/")) {
                val uncPath = wslCompat.convertLinuxPathToWindowsUnc(distribution, filePath)
                if (uncPath != null) {
                    file = LocalFileSystem.getInstance().findFileByPath(uncPath)
                    if (file != null) {
                        LOGGER.debug("WSL path resolved: $filePath → $uncPath")
                    } else {
                        LOGGER.debug("WSL path conversion failed to resolve: $filePath → $uncPath")
                    }
                }
            }

            if (file == null) {
                LOGGER.debug("Cannot resolve file path: $filePath")
            }

            val sourcePosition = XDebuggerUtil.getInstance().createPosition(file, line)
            return sourcePosition?.let { SourcePosition(it) }
        }

        @JvmStatic
        fun create(snapshot: ProcessSnapshot, distribution: WSLDistribution? = null): SourcePosition? =
            create(snapshot.stack[0], distribution)

        @JvmStatic
        fun create(traceElement: TraceElement, distribution: WSLDistribution? = null): SourcePosition? =
            create(traceElement.file, traceElement.line - 1, distribution)
    }
}
