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
import org.elixir_lang.debugger.node.ElixirDebuggerEventListener;
import org.elixir_lang.debugger.node.ElixirDebuggerNode;

class DebugRemoteNodeResponseEvent extends ErlangDebuggerEvent {
  public static final String NAME = "debug_remote_node_response";

  private final String myNodeName;
  private final String myError;

  public DebugRemoteNodeResponseEvent(OtpErlangTuple receivedMessage) throws DebuggerEventFormatException {
    myNodeName = OtpErlangTermUtil.getAtomText(receivedMessage.elementAt(1));
    OtpErlangObject status = receivedMessage.elementAt(2);
    myError = "ok".equals(OtpErlangTermUtil.getAtomText(status)) ? null : status.toString();
  }

  @Override
  public void process(ElixirDebuggerNode debuggerNode, ElixirDebuggerEventListener eventListener) {
    if (myError != null) {
      eventListener.failedToDebugRemoteNode(myNodeName, myError);
    }
  }
}
