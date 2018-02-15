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

package org.elixir_lang.debugger;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import org.elixir_lang.debugger.node.ProcessSnapshot;
import org.elixir_lang.debugger.node.TraceElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SourcePosition {
  @NotNull
  private final XSourcePosition mySourcePosition;

  private SourcePosition(@NotNull XSourcePosition sourcePosition) {
    mySourcePosition = sourcePosition;
  }

  @Contract(pure = true)
  @NotNull
  XSourcePosition getSourcePosition() {
    return mySourcePosition;
  }

  public int getLine() {
    return mySourcePosition.getLine();
  }

  @NotNull
  public VirtualFile getFile() {
    return mySourcePosition.getFile();
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SourcePosition that = (SourcePosition) o;

    return getLine() == that.getLine() && getFile().equals(that.getFile());
  }

  @Override
  public int hashCode() {
    int result = getFile().hashCode();
    result = 31 * result + getLine();
    return result;
  }

  @NotNull
  public static SourcePosition create(@NotNull XSourcePosition position) {
    return new SourcePosition(position);
  }

  @Nullable
  public static SourcePosition create(@NotNull String filePath, int line) {
    VirtualFile file = LocalFileSystem.getInstance().findFileByPath(filePath);
    XSourcePosition sourcePosition = XDebuggerUtil.getInstance().createPosition(file, line);
    if (sourcePosition != null) {
      return new SourcePosition(sourcePosition);
    } else {
      return null;
    }
  }

  @Nullable
  public static SourcePosition create(@NotNull ProcessSnapshot snapshot) {
    return create(snapshot.getStack().get(0));
  }

  @Nullable
  public static SourcePosition create(@NotNull TraceElement traceElement) {
    return create(traceElement.getFile(), traceElement.getLine() - 1);
  }
}
