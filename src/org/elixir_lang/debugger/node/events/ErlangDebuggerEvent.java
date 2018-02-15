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

package org.elixir_lang.debugger.node.events;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;
import org.elixir_lang.debugger.node.DebuggerEventListener;
import org.elixir_lang.debugger.node.DebuggerNode;
import org.jetbrains.annotations.Nullable;

public abstract class ErlangDebuggerEvent {

  public abstract void process(DebuggerNode debuggerNode, DebuggerEventListener eventListener);

  @Nullable
  public static ErlangDebuggerEvent create(OtpErlangObject message) {
    if (!(message instanceof OtpErlangTuple)) return null;
    OtpErlangTuple messageTuple = (OtpErlangTuple) message;
    String messageName = OtpErlangTermUtil.getAtomText(messageTuple.elementAt(0));
    if (messageName == null) return null;

    try {
      if (InterpretModulesResponseEvent.NAME.equals(messageName)) return new InterpretModulesResponseEvent(messageTuple);
      if (SetBreakpointResponseEvent.NAME.equals(messageName)) return new SetBreakpointResponseEvent(messageTuple);
      if (BreakpointReachedEvent.NAME.equals(messageName)) return new BreakpointReachedEvent(messageTuple);
      if (DebugRemoteNodeResponseEvent.NAME.equals(messageName)) return new DebugRemoteNodeResponseEvent(messageTuple);
    } catch (DebuggerEventFormatException e) {
      return new UnknownMessageEvent(messageTuple);
    }
    return new UnknownMessageEvent(messageTuple);
  }
}
