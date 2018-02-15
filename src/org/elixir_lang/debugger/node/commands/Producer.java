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

package org.elixir_lang.debugger.node.commands;

import com.ericsson.otp.erlang.*;
import org.jetbrains.annotations.NotNull;

public final class Producer {
  private Producer() {
  }

  @NotNull
  public static ErlangDebuggerCommand getSetBreakpointCommand(@NotNull String module, int line, @NotNull String file) {
    return new SetBreakpointCommand(module, line, file);
  }

  @NotNull
  public static ErlangDebuggerCommand getRemoveBreakpointCommand(@NotNull String module, int line) {
    return new RemoveBreakpointCommand(module, line);
  }

  @NotNull
  public static ErlangDebuggerCommand getStepIntoCommand(@NotNull OtpErlangPid pid) {
    return new StepIntoCommand(pid);
  }

  @NotNull
  public static ErlangDebuggerCommand getStepOverCommand(@NotNull OtpErlangPid pid) {
    return new StepOverCommand(pid);
  }

  @NotNull
  public static ErlangDebuggerCommand getStepOutCommand(@NotNull OtpErlangPid pid) {
    return new StepOutCommand(pid);
  }

  @NotNull
  public static ErlangDebuggerCommand getContinueCommand(@NotNull OtpErlangPid pid) {
    return new ContinueCommand(pid);
  }

  @NotNull
  public static ErlangDebuggerCommand getRunTaskCommand() {
    return new RunTaskCommand();
  }

  private static class StepOverCommand extends AbstractPidCommand {
    StepOverCommand(@NotNull OtpErlangPid pid) {
      super("step_over", pid);
    }
  }

  private static class SetBreakpointCommand implements ErlangDebuggerCommand {
    private final String myModule;
    private final int myLine;
    private final String myFile;

    SetBreakpointCommand(@NotNull String module, int line, @NotNull String file) {
      myModule = module;
      myLine = line + 1;
      myFile = file;
    }

    @NotNull
    @Override
    public OtpErlangTuple toMessage() {
      return new OtpErlangTuple(new OtpErlangObject[]{
        new OtpErlangAtom("set_breakpoint"),
        new OtpErlangAtom(myModule),
        new OtpErlangInt(myLine),
        new OtpErlangString(myFile)
      });
    }
  }

  private static abstract class AbstractPidCommand implements ErlangDebuggerCommand {
    private final String myName;
    private final OtpErlangPid myPid;

    AbstractPidCommand(@NotNull String cmdName, @NotNull OtpErlangPid pid) {
      myName = cmdName;
      myPid = pid;
    }

    @NotNull
    @Override
    public OtpErlangTuple toMessage() {
      return new OtpErlangTuple(new OtpErlangObject[]{new OtpErlangAtom(myName), myPid});
    }
  }

  private static class StepOutCommand extends AbstractPidCommand {
    StepOutCommand(@NotNull OtpErlangPid pid) {
      super("step_out", pid);
    }
  }

  private static class StepIntoCommand extends AbstractPidCommand {
    StepIntoCommand(@NotNull OtpErlangPid pid) {
      super("step_into", pid);
    }
  }

  private static class ContinueCommand extends AbstractPidCommand {
    ContinueCommand(@NotNull OtpErlangPid pid) {
      super("continue", pid);
    }
  }

  private static class RemoveBreakpointCommand implements ErlangDebuggerCommand {
    private final String myModule;
    private final int myLine;

    RemoveBreakpointCommand(@NotNull String module, int line) {
      myModule = module;
      myLine = line + 1;
    }

    @NotNull
    @Override
    public OtpErlangTuple toMessage() {
      return new OtpErlangTuple(new OtpErlangObject[] {
        new OtpErlangAtom("remove_breakpoint"),
        new OtpErlangAtom(myModule),
        new OtpErlangInt(myLine)
      });
    }
  }

  private static class RunTaskCommand implements ErlangDebuggerCommand {
    @NotNull
    @Override
    public OtpErlangTuple toMessage() {
      return new OtpErlangTuple(new OtpErlangAtom("run_task"));
    }
  }

  public interface ErlangDebuggerCommand {
    @NotNull
    OtpErlangTuple toMessage();
  }
}
