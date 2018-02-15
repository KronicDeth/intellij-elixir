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

package org.elixir_lang.debugger.xdebug

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.util.Processor
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.breakpoints.XLineBreakpointType
import org.elixir_lang.ElixirFileType
import org.elixir_lang.psi.impl.ElixirPsiImplUtil
import org.jetbrains.annotations.Contract

class LineBreakpointType private constructor() : XLineBreakpointType<LineBreakpointProperties>(ID, NAME) {
    override fun canPutAt(file: VirtualFile, line: Int, project: Project): Boolean =
            file.fileType === ElixirFileType.INSTANCE && isLineBreakpointAvailable(file, line, project)

    override fun createBreakpointProperties(file: VirtualFile, line: Int): LineBreakpointProperties =
            LineBreakpointProperties()

    private class LineBreakpointAvailabilityProcessor : Processor<PsiElement> {
        @get:Contract(pure = true)
        internal var isLineBreakpointAvailable: Boolean = false
            private set

        override fun process(psiElement: PsiElement): Boolean =
                if (ElixirPsiImplUtil.getModuleName(psiElement) != null) {
                    isLineBreakpointAvailable = true
                    false
                } else {
                    true
                }
    }

    companion object {
        private const val ID = "ElixirLineBreakpoint"
        private const val NAME = "Line breakpoint"

        // TODO: it should return true for lines matching "Executable Lines"
        // description at http://www.erlang.org/doc/apps/debugger/debugger_chapter.html
        // and, ideally, it should return false otherwise
        private fun isLineBreakpointAvailable(file: VirtualFile, line: Int, project: Project): Boolean =
            FileDocumentManager.getInstance().getDocument(file)?.let { document ->
                val canPutAtChecker = LineBreakpointAvailabilityProcessor()
                XDebuggerUtil.getInstance().iterateLine(project, document, line, canPutAtChecker)

                canPutAtChecker.isLineBreakpointAvailable
            } ?: false
    }
}
