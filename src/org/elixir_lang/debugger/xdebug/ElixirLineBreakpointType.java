/*
 * Copyright 2012-2014 Sergey Ignatov
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

package org.elixir_lang.debugger.xdebug;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.Processor;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import org.elixir_lang.ElixirFileType;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirLineBreakpointType extends XLineBreakpointType<ElixirLineBreakpointProperties> {
  private static final String ID = "ElixirLineBreakpoint";
  private static final String NAME = "Line breakpoint";

  protected ElixirLineBreakpointType() {
    super(ID, NAME);
  }

  @Nullable
  @Override
  public ElixirLineBreakpointProperties createBreakpointProperties(@NotNull VirtualFile file, int line) {
    return new ElixirLineBreakpointProperties();
  }

  @Override
  public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project) {
    return file.getFileType() == ElixirFileType.INSTANCE && isLineBreakpointAvailable(file, line, project);
  }

  // TODO: it should return true for lines matching "Executable Lines"
  // description at http://www.erlang.org/doc/apps/debugger/debugger_chapter.html
  // and, ideally, it should return false otherwise
  private static boolean isLineBreakpointAvailable(@NotNull VirtualFile file, int line, @NotNull Project project) {
    Document document = FileDocumentManager.getInstance().getDocument(file);
    if (document == null) return false;
    LineBreakpointAvailabilityProcessor canPutAtChecker = new LineBreakpointAvailabilityProcessor();
    XDebuggerUtil.getInstance().iterateLine(project, document, line, canPutAtChecker);
    return canPutAtChecker.isLineBreakpointAvailable();
  }

  private static final class LineBreakpointAvailabilityProcessor implements Processor<PsiElement> {
    private boolean myIsLineBreakpointAvailable;

    @Override
    public boolean process(PsiElement psiElement) {
      if (ElixirPsiImplUtil.getModuleName(psiElement) != null) {
        myIsLineBreakpointAvailable = true;
        return false;
      }
      return true;
    }

    public boolean isLineBreakpointAvailable() {
      return myIsLineBreakpointAvailable;
    }
  }
}
