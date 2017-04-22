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

package org.elixir_lang.debugger.xdebug;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.icons.AllIcons;
import com.intellij.ui.ColoredTextContainer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.frame.XValueChildrenList;
import org.elixir_lang.debugger.node.ElixirTraceElement;
import org.elixir_lang.debugger.node.ElixirVariableBinding;
import org.elixir_lang.debugger.xdebug.xvalue.ElixirXValueFactory;
import org.elixir_lang.utils.ElixirModulesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ElixirStackFrame extends XStackFrame {
  @NotNull
  private final ElixirTraceElement myTraceElement;
  @Nullable
  private final ElixirSourcePosition mySourcePosition;

  ElixirStackFrame(@NotNull ElixirTraceElement traceElement) {
    myTraceElement = traceElement;
    mySourcePosition = ElixirSourcePosition.create(traceElement);
  }

  @Nullable
  @Override
  public XSourcePosition getSourcePosition() {
    if (mySourcePosition == null) return null;
    return mySourcePosition.getSourcePosition();
  }

  @Override
  public void customizePresentation(@NotNull ColoredTextContainer component) {
    String title = ElixirModulesUtil.erlangModuleNameToElixir(myTraceElement.getModule()) +
      "." + myTraceElement.getFunction() + "/" + myTraceElement.getFunctionArgs().arity();
    if (myTraceElement.getLine() > 1) title += ", line " + myTraceElement.getLine();
    component.append(title, SimpleTextAttributes.REGULAR_ATTRIBUTES);
    component.setIcon(AllIcons.Debugger.StackFrame);
  }

  @Override
  public void computeChildren(@NotNull XCompositeNode node) {
    XValueChildrenList myVariables = new XValueChildrenList(myTraceElement.getBindings().size());
    for (ElixirVariableBinding binding : myTraceElement.getBindings()) {
      myVariables.add(binding.getName(), getVariableValue(binding.getValue()));
    }
    node.addChildren(myVariables, true);
  }

  @NotNull
  private static XValue getVariableValue(OtpErlangObject value) {
    return ElixirXValueFactory.create(value);
  }
}
