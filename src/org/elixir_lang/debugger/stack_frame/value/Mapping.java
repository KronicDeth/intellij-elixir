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

package org.elixir_lang.debugger.stack_frame.value;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValueChildrenList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class Mapping extends Base<OtpErlangTuple> {
  Mapping(OtpErlangObject key, OtpErlangObject value) {
    super(new OtpErlangTuple(new OtpErlangObject[]{key, value}), 2);
  }

  @Override
  public void computeChildren(@NotNull XCompositeNode node) {
    XValueChildrenList children = new XValueChildrenList(2);
    addNamedChild(children, getMappingKey(), "key");
    addNamedChild(children, getMappingValue(), "value");
    node.addChildren(children, true);
  }

  @Nullable
  @Override
  protected java.lang.String getType() {
    return "Mapping";
  }

  @NotNull
  @Override
  protected java.lang.String getStringRepr() {
    return getMappingKey() + " => " + getMappingValue();
  }

  private OtpErlangObject getMappingKey() {
    return getValue().elementAt(0);
  }

  private OtpErlangObject getMappingValue() {
    return getValue().elementAt(1);
  }
}
