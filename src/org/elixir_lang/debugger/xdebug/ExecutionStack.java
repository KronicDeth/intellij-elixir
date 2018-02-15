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

package org.elixir_lang.debugger.xdebug;

import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XStackFrame;
import org.elixir_lang.debugger.node.ProcessSnapshot;
import org.elixir_lang.debugger.node.TraceElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

class ExecutionStack extends XExecutionStack {
  @NotNull
  private final ProcessSnapshot myProcessSnapshot;
  @NotNull
  private final List<StackFrame> myStack;

  public ExecutionStack(@NotNull ProcessSnapshot snapshot) {
    super(snapshot.getPidString());
    myProcessSnapshot = snapshot;
    myStack = new ArrayList<>(snapshot.getStack().size());
  }

  @Nullable
  @Override
  public XStackFrame getTopFrame() {
    return ContainerUtil.getFirstItem(myStack);
  }

  @Override
  public void computeStackFrames(int firstFrameIndex, @NotNull XStackFrameContainer container) {
    if (myStack.isEmpty()) {
      List<TraceElement> traceElements = myProcessSnapshot.getStack();
      for (TraceElement traceElement : traceElements) {
        myStack.add(new StackFrame(traceElement));
      }
      container.addStackFrames(myStack, true);
    }
  }
}
