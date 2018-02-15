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

package org.elixir_lang.debugger.node;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;
import org.elixir_lang.debugger.Node;
import org.elixir_lang.debugger.node.event.*;
import org.jetbrains.annotations.Nullable;

public abstract class Event {

  public abstract void process(Node node, Listener eventListener);

  @Nullable
  public static Event create(OtpErlangObject message) {
    if (!(message instanceof OtpErlangTuple)) return null;
    OtpErlangTuple messageTuple = (OtpErlangTuple) message;
    String messageName = OtpErlangTermUtil.getAtomText(messageTuple.elementAt(0));
    if (messageName == null) return null;

    try {
      if (InterpretModulesResponse.NAME.equals(messageName)) return new InterpretModulesResponse(messageTuple);
      if (SetBreakpointResponse.NAME.equals(messageName)) return new SetBreakpointResponse(messageTuple);
      if (BreakpointReached.NAME.equals(messageName)) return new BreakpointReached(messageTuple);
      if (DebugRemoteNodeResponse.NAME.equals(messageName)) return new DebugRemoteNodeResponse(messageTuple);
    } catch (FormatException e) {
      return new UnknownMessage(messageTuple);
    }
    return new UnknownMessage(messageTuple);
  }
}
