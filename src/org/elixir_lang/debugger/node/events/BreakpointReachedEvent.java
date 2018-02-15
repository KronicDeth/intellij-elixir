/*
 * Copyright 2012-2014 Sergey Ignatov
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

package org.elixir_lang.debugger.node.events;

import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.debugger.node.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.elixir_lang.debugger.node.events.OtpErlangTermUtil.*;

class BreakpointReachedEvent extends ErlangDebuggerEvent {
  public static final String NAME = "breakpoint_reached";

  private final OtpErlangPid myActivePid;
  private final List<ProcessSnapshot> mySnapshots;

  BreakpointReachedEvent(OtpErlangTuple breakpointReachedMessage) throws DebuggerEventFormatException {
    OtpErlangPid activePid = getPidValue(elementAt(breakpointReachedMessage, 1));
    OtpErlangList snapshots = getListValue(elementAt(breakpointReachedMessage, 2));
    if (activePid == null || snapshots == null) throw new DebuggerEventFormatException();

    myActivePid = activePid;
    mySnapshots = new ArrayList<>(snapshots.arity());
    for (OtpErlangObject snapshot : snapshots) {
      OtpErlangTuple snapshotTuple = getTupleValue(snapshot); // {Pid, Function, Status, Info, Stack}

      OtpErlangPid pid = getPidValue(elementAt(snapshotTuple, 0));
      String status = getAtomText(elementAt(snapshotTuple, 2));
      OtpErlangObject info = elementAt(snapshotTuple, 3);
      List<TraceElement> stack = getStack(getListValue(elementAt(snapshotTuple, 4)));

      if (pid == null /*|| init == null*/ || status == null || info == null || stack == null) {
        throw new DebuggerEventFormatException();
      }

      if ("break".equals(status)) {
        OtpErlangTuple infoTuple = getTupleValue(info);
        String breakModule = getAtomText(elementAt(infoTuple, 0));
        Integer breakLine = getIntegerValue(elementAt(infoTuple, 1));
        String breakFile = getStringText(elementAt(infoTuple, 2));

        if (breakLine == null || breakModule == null || breakFile == null) {
          throw new DebuggerEventFormatException();
        }
        mySnapshots.add(new ProcessSnapshot(pid, stack));
      }
      else if ("exit".equals(status)) {
        mySnapshots.add(new ProcessSnapshot(pid, stack));
      }
      else {
        mySnapshots.add(new ProcessSnapshot(pid, stack));
      }
    }
  }

  @Override
  public void process(@NotNull DebuggerNode debuggerNode, @NotNull DebuggerEventListener eventListener) {
    debuggerNode.processSuspended(myActivePid);
    eventListener.breakpointReached(myActivePid, mySnapshots);
  }

  @Nullable
  private static List<TraceElement> getStack(@Nullable OtpErlangList traceElementsList) {
    if (traceElementsList == null) return null;
    List<TraceElement> stack = new ArrayList<>(traceElementsList.arity());
    for (OtpErlangObject traceElementObject : traceElementsList) {
      OtpErlangTuple traceElementTuple = getTupleValue(traceElementObject);
      // ignoring SP at 0
      OtpErlangTuple moduleFunctionArgsTuple = getTupleValue(elementAt(traceElementTuple, 1));
      OtpErlangList bindingsList = getListValue(elementAt(traceElementTuple, 2));
      OtpErlangTuple fileLineTuple = getTupleValue(elementAt(traceElementTuple, 3));
      String file = getStringText(elementAt(fileLineTuple, 0));
      Integer line = getIntegerValue(elementAt(fileLineTuple, 1));

      TraceElement traceElement = getTraceElement(moduleFunctionArgsTuple, bindingsList, file, line);
      if (traceElement == null) return null;
      stack.add(traceElement);
    }
    return stack;
  }

  @Nullable
  private static TraceElement getTraceElement(@Nullable OtpErlangTuple moduleFunctionArgsTuple,
                                              @Nullable OtpErlangList bindingsList,
                                              String file,
                                              Integer line) {

    String moduleName = getAtomText(elementAt(moduleFunctionArgsTuple, 0));
    String functionName = getAtomText(elementAt(moduleFunctionArgsTuple, 1));
    OtpErlangList args = getListValue(elementAt(moduleFunctionArgsTuple, 2));
    Collection<VariableBinding> bindings = getBindings(bindingsList);
    if (moduleName == null || functionName == null || args == null) return null; // bindings are not necessarily present
    return new TraceElement(moduleName, functionName, args, bindings, file, line);
  }

  @NotNull
  private static Collection<VariableBinding> getBindings(@Nullable OtpErlangList bindingsList) {
    if (bindingsList == null) return ContainerUtil.emptyList();
    Collection<VariableBinding> bindings = new ArrayList<>(bindingsList.arity());
    for (OtpErlangObject bindingObject : bindingsList) {
      OtpErlangTuple bindingTuple = getTupleValue(bindingObject);
      String variableName = getAtomText(elementAt(bindingTuple, 0));
      OtpErlangObject variableValue = elementAt(bindingTuple, 1);
      if (variableName == null || variableValue == null) return ContainerUtil.emptyList();
      bindings.add(new VariableBinding(variableName, variableValue));
    }
    return bindings;
  }
}
