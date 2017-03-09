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

package org.elixir_lang.debugger.node.commands;

import com.ericsson.otp.erlang.*;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ElixirDebuggerCommandsProducer {
  private ElixirDebuggerCommandsProducer() {
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
  public static ErlangDebuggerCommand getRunDebuggerCommand(@NotNull String module, @NotNull String function, @NotNull List<String> args) {
    return new RunDebuggerCommand(module, function, args);
  }

  @NotNull
  public static ErlangDebuggerCommand getInterpretModulesCommand(@NotNull List<String> moduleSourcePaths) {
    return new InterpretModulesCommand(moduleSourcePaths);
  }

  @NotNull
  public static ErlangDebuggerCommand getDebugRemoteNodeCommand(@NotNull String nodeName, @Nullable String cookie) {
    return new DebugRemoteNodeCommand(nodeName, cookie);
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
    public StepOverCommand(@NotNull OtpErlangPid pid) {
      super("step_over", pid);
    }
  }

  private static class RunDebuggerCommand implements ErlangDebuggerCommand {
    private final String myModule;
    private final String myFunction;
    private final List<String> myArgs;

    RunDebuggerCommand(@NotNull String module, @NotNull String function, @NotNull List<String> args) {
      myModule = module;
      myFunction = function;
      myArgs = args;
    }

    @NotNull
    @Override
    public OtpErlangTuple toMessage() {
      OtpErlangObject erlangArgs[] = new OtpErlangObject[myArgs.size()];
      for (int i = 0; i < myArgs.size(); ++i) {
        erlangArgs[i] = new OtpErlangString(myArgs.get(i));
      }

      myArgs.stream().map(e -> new OtpErlangString(e)).toArray();

      return new OtpErlangTuple(new OtpErlangObject[] {
        new OtpErlangAtom("run_debugger"),
        new OtpErlangAtom(myModule),
        new OtpErlangAtom(myFunction),
        new OtpErlangList(erlangArgs)
      });
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

    protected AbstractPidCommand(@NotNull String cmdName, @NotNull OtpErlangPid pid) {
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
    public StepOutCommand(@NotNull OtpErlangPid pid) {
      super("step_out", pid);
    }
  }

  private static class StepIntoCommand extends AbstractPidCommand {
    public StepIntoCommand(@NotNull OtpErlangPid pid) {
      super("step_into", pid);
    }
  }

  private static class ContinueCommand extends AbstractPidCommand {
    protected ContinueCommand(@NotNull OtpErlangPid pid) {
      super("continue", pid);
    }
  }

  private static class InterpretModulesCommand implements ErlangDebuggerCommand {
    private final List<String> myModuleSourcePaths;

    public InterpretModulesCommand(@NotNull List<String> moduleSourcePaths) {
      myModuleSourcePaths = moduleSourcePaths;
    }

    @NotNull
    @Override
    public OtpErlangTuple toMessage() {
      OtpErlangObject[] moduleSourcePaths = new OtpErlangObject[myModuleSourcePaths.size()];
      for (int i = 0; i < myModuleSourcePaths.size(); i++) {
        moduleSourcePaths[i] = new OtpErlangString(myModuleSourcePaths.get(i));
      }
      return new OtpErlangTuple(new OtpErlangObject[] {
        new OtpErlangAtom("interpret_modules"),
        new OtpErlangList(moduleSourcePaths)
      });
    }
  }

  private static class DebugRemoteNodeCommand implements ErlangDebuggerCommand {
    private final String myNodeName;
    private final String myCookie;

    public DebugRemoteNodeCommand(@NotNull String nodeName, @Nullable String cookie) {
      myNodeName = nodeName;
      myCookie = !StringUtil.isEmptyOrSpaces(cookie) ? cookie : "nocookie";
    }

    @NotNull
    @Override
    public OtpErlangTuple toMessage() {
      return new OtpErlangTuple(new OtpErlangObject[] {
        new OtpErlangAtom("debug_remote_node"),
        new OtpErlangAtom(myNodeName),
        new OtpErlangAtom(myCookie)
      });
    }
  }

  private static class RemoveBreakpointCommand implements ErlangDebuggerCommand {
    private final String myModule;
    private final int myLine;

    public RemoveBreakpointCommand(@NotNull String module, int line) {
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
