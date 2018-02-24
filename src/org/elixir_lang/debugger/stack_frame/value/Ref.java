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

import com.ericsson.otp.erlang.OtpErlangRef;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValueChildrenList;
import org.jetbrains.annotations.NotNull;

public class Ref extends Base<OtpErlangRef> {
  Ref(@NotNull OtpErlangRef value) {
    super(value, 1 + value.ids().length);
  }

  @Override
  public void computeChildren(@NotNull XCompositeNode node) {
    int[] ids = getValue().ids();
    XValueChildrenList children = new XValueChildrenList(1 + ids.length);
    addNamedChild(children, getValue().node(), "node");
    for (int i = 0; i < ids.length; i++) {
      addNamedChild(children, ids[i], "id" + i);
    }
    node.addChildren(children, true);
  }
}
