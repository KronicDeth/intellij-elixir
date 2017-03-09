/*
 * Copyright 2012-2015 Sergey Ignatov
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

import com.ericsson.otp.erlang.OtpErlangPid;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.ResourceUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.io.URLUtil;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.evaluation.EvaluationMode;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import org.elixir_lang.ElixirFileType;
import org.elixir_lang.debugger.node.ElixirDebuggerEventListener;
import org.elixir_lang.debugger.node.ElixirDebuggerNode;
import org.elixir_lang.debugger.node.ElixirDebuggerNodeException;
import org.elixir_lang.debugger.node.ElixirProcessSnapshot;
import org.elixir_lang.mix.runner.MixRunConfigurationBase;
import org.elixir_lang.mix.runner.MixRunningState;
import org.elixir_lang.mix.runner.MixRunningStateUtil;
import org.elixir_lang.mix.runner.exunit.MixExUnitRunConfiguration;
import org.elixir_lang.psi.ElixirFile;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.elixir_lang.debugger.ElixirDebuggerLog.LOG;

class ElixirXDebugProcess extends XDebugProcess implements ElixirDebuggerEventListener {
  private final ExecutionEnvironment myExecutionEnvironment;
  @NotNull
  private final MixRunningState myRunningState;
  @NotNull
  private final ElixirDebuggerNode myDebuggerNode;
  @NotNull
  private final OSProcessHandler myElixirProcessHandler;

  @NotNull
  private final XBreakpointHandler<?>[] myBreakpointHandlers = new XBreakpointHandler[]{new ElixirLineBreakpointHandler(this)};
  @NotNull
  private final ConcurrentHashMap<ElixirSourcePosition, XLineBreakpoint<ElixirLineBreakpointProperties>> myPositionToLineBreakpointMap =
    new ConcurrentHashMap<>();

  public ElixirXDebugProcess(@NotNull XDebugSession session, ExecutionEnvironment env) throws ExecutionException {
    super(session);

    session.setPauseActionSupported(false);

    myExecutionEnvironment = env;
    myRunningState = (MixRunningState)getRunConfiguration().getState(myExecutionEnvironment.getExecutor(), myExecutionEnvironment);

    try {
      //TODO add the debugger node to disposable hierarchy (we may fail to initialize session so the session will not be stopped!)
      myDebuggerNode = new ElixirDebuggerNode(this);
    }
    catch (ElixirDebuggerNodeException e) {
      throw new ExecutionException(e);
    }

    //TODO split running debug target and debugger process spawning
    myElixirProcessHandler = runDebugTarget();
  }

  @Nullable
  private static ElixirSourcePosition getElixirSourcePosition(@NotNull XLineBreakpoint<ElixirLineBreakpointProperties> breakpoint) {
    XSourcePosition sourcePosition = breakpoint.getSourcePosition();

    return sourcePosition != null ? ElixirSourcePosition.create(sourcePosition) : null;
  }

  private static List<String> setUpElixirDebuggerCodePath() throws ExecutionException {
    LOG.debug("Setting up debugger environment.");
    try {
      String[] files = {"Elixir.Mix.Tasks.IntellijElixir.DebugTask.beam", "Elixir.IntellijElixir.DebugServer.beam"};
      File tempDirectory = FileUtil.createTempDirectory("intellij_elixir_debugger", null);
      LOG.debug("Debugger elixir files will be put to: " + tempDirectory.getPath());
      for (String file : files) {
        copyFileTo(file, tempDirectory);
      }
      LOG.debug("Debugger elixir files were copied successfully.");
      return Arrays.asList("-pa", tempDirectory.getPath());
    }
    catch (IOException e) {
      throw new ExecutionException("Failed to setup debugger environment", e);
    }
  }

  private static void copyFileTo(@NotNull String fileName, File directory) throws IOException {
    URL fileUrl = ResourceUtil.getResource(ElixirXDebugProcess.class, "/debugger/_build/shared/lib/intellij_elixir_debugger/ebin", fileName);
    if (fileUrl == null) {
      throw new IOException("Failed to locate debugger module: " + fileName);
    }
    try (BufferedInputStream inputStream = new BufferedInputStream(URLUtil.openStream(fileUrl))) {
      try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(directory, fileName)))) {
        FileUtil.copy(inputStream, outputStream);
      }
    }
  }

  @Override
  public void debuggerStarted() {
    getSession().reportMessage("Debug process started", MessageType.INFO);
  }

  @Override
  public void sessionInitialized() {
    myDebuggerNode.runTask();
  }

  @Override
  public void failedToInterpretModules(String nodeName, @NotNull List<String> modules) {
    String messagePrefix = "Failed to interpret modules on node " + nodeName + ": ";
    String modulesString = StringUtil.join(modules, ", ");
    String messageSuffix = ".\nMake sure they are compiled with debug_info option, their sources are located in same directory as .beam files, modules are available on the node.";
    String message = messagePrefix + modulesString + messageSuffix;
    getSession().reportMessage(message, MessageType.WARNING);
  }

  @Override
  public void failedToDebugRemoteNode(String nodeName, String error) {
    String message = "Failed to debug remote node '" + nodeName + "'. Details: " + error;
    getSession().reportMessage(message, MessageType.ERROR);
  }

  @Override
  public void unknownMessage(String messageText) {
    getSession().reportMessage("Unknown message received: " + messageText, MessageType.WARNING);
  }

  @Override
  public void failedToSetBreakpoint(String module, @NotNull String file, int line, String errorMessage) {
    ElixirSourcePosition sourcePosition = ElixirSourcePosition.create(file, line);
    XLineBreakpoint<ElixirLineBreakpointProperties> breakpoint = getLineBreakpoint(sourcePosition);
    if (breakpoint != null) {
      getSession().updateBreakpointPresentation(breakpoint, AllIcons.Debugger.Db_invalid_breakpoint, errorMessage);
    }
    getSession().reportMessage("Failed to set breakpoint. Module: " + module + " Line: " + (line + 1), MessageType.WARNING);
  }

  @Override
  public void breakpointIsSet(String module, String file, int line) {
  }

  @Override
  public void breakpointReached(@NotNull final OtpErlangPid pid, @NotNull List<ElixirProcessSnapshot> snapshots) {
    ElixirProcessSnapshot processInBreakpoint = ContainerUtil.find(snapshots, elixirProcessSnapshot -> elixirProcessSnapshot.getPid().equals(pid));
    assert processInBreakpoint != null;
    ElixirSourcePosition breakPosition = ElixirSourcePosition.create(processInBreakpoint);
    XLineBreakpoint<ElixirLineBreakpointProperties> breakpoint = getLineBreakpoint(breakPosition);
    ElixirSuspendContext suspendContext = new ElixirSuspendContext(pid, snapshots);
    if (breakpoint == null) {
      getSession().positionReached(suspendContext);
    }
    else {
      boolean shouldSuspend = getSession().breakpointReached(breakpoint, null, suspendContext);
      if (!shouldSuspend) {
        resume();
      }
    }
  }

  @Override
  public void debuggerStopped() {
    getSession().reportMessage("Debug process stopped", MessageType.INFO);
    getSession().stop();
  }

  @Nullable
  private XLineBreakpoint<ElixirLineBreakpointProperties> getLineBreakpoint(@Nullable ElixirSourcePosition sourcePosition) {
    return sourcePosition != null ? myPositionToLineBreakpointMap.get(sourcePosition) : null;
  }

  @NotNull
  @Override
  public ExecutionConsole createConsole() {
    ConsoleView consoleView = myRunningState.createConsoleView(myExecutionEnvironment.getExecutor());
    consoleView.attachToProcess(getProcessHandler());
    myElixirProcessHandler.startNotify();
    return consoleView;
  }

  @NotNull
  @Override
  public XBreakpointHandler<?>[] getBreakpointHandlers() {
    return myBreakpointHandlers;
  }

  @NotNull
  @Override
  public XDebuggerEditorsProvider getEditorsProvider() {
    return new XDebuggerEditorsProvider() {
      @NotNull
      @Override
      public FileType getFileType() {
        return ElixirFileType.INSTANCE;
      }

      @NotNull
      @Override
      public Document createDocument(@NotNull Project project,
                                     @NotNull String text,
                                     @Nullable XSourcePosition sourcePosition,
                                     @NotNull EvaluationMode mode) {
        LightVirtualFile file = new LightVirtualFile("plain-text-elixir-debugger.txt", text);
        //noinspection ConstantConditions
        return FileDocumentManager.getInstance().getDocument(file);
      }
    };
  }

  @Override
  public void startStepOver() {
    myDebuggerNode.stepOver();
  }

  @Override
  public void startStepInto() {
    myDebuggerNode.stepInto();
  }

  @Override
  public void startStepOut() {
    myDebuggerNode.stepOut();
  }

  @Override
  public void stop() {
    myDebuggerNode.stop();
  }

  @Override
  public void resume() {
    myDebuggerNode.resume();
  }

  @Override
  public void runToPosition(@NotNull XSourcePosition position) {
    //TODO implement me
  }

  @Nullable
  @Override
  protected ProcessHandler doGetProcessHandler() {
    return myElixirProcessHandler;
  }

  void addBreakpoint(@NotNull XLineBreakpoint<ElixirLineBreakpointProperties> breakpoint) {
    ElixirSourcePosition breakpointPosition = getElixirSourcePosition(breakpoint);
    if (breakpointPosition == null) return;
    String moduleName = getModuleName(breakpointPosition);
    if (moduleName != null) {
      myPositionToLineBreakpointMap.put(breakpointPosition, breakpoint);
      myDebuggerNode.setBreakpoint(moduleName, breakpointPosition.getFile().getPath(), breakpointPosition.getLine());
    } else {
      final String message =
        "Unable to determine module for breakpoint at " +
          breakpointPosition.getFile() + " line " + breakpointPosition.getLine();
      getSession().reportMessage(message, MessageType.ERROR);
    }
  }

  @Nullable
  private String getModuleName(@NotNull ElixirSourcePosition breakpointPosition) {
    ElixirFile psiFile = (ElixirFile)PsiManager.getInstance(getRunConfiguration().getProject()).findFile(breakpointPosition.getFile());
    if (psiFile == null) return null;

    PsiElement elem = psiFile.findElementAt(breakpointPosition.getSourcePosition().getOffset());
    return ElixirPsiImplUtil.getModuleName(elem);
  }

  void removeBreakpoint(@NotNull XLineBreakpoint<ElixirLineBreakpointProperties> breakpoint,
                        @SuppressWarnings("UnusedParameters") boolean temporary) {
    ElixirSourcePosition breakpointPosition = getElixirSourcePosition(breakpoint);
    if (breakpointPosition == null) return;
    myPositionToLineBreakpointMap.remove(breakpointPosition);
    String moduleName = getModuleName(breakpointPosition);
    if (moduleName != null) {
      myDebuggerNode.removeBreakpoint(moduleName, breakpointPosition.getLine());
    }
  }

  @NotNull
  private MixRunConfigurationBase getRunConfiguration() {
    MixRunConfigurationBase runConfig = (MixRunConfigurationBase) getSession().getRunProfile();
    assert runConfig != null;
    return runConfig;
  }

  @NotNull
  private OSProcessHandler runDebugTarget() throws ExecutionException {
    OSProcessHandler elixirProcessHandler;
    LOG.debug("Preparing to run debug target.");

    ArrayList<String> elixirParams = new ArrayList<>();
    elixirParams.addAll(myRunningState.setupElixirParams());
    elixirParams.addAll(setUpElixirDebuggerCodePath());

    List<String> mixParams = new ArrayList<>();
    mixParams.addAll(Arrays.asList("intellij_elixir.debug_task", "--debugger-port", "" + myDebuggerNode.getLocalDebuggerPort(), "--"));
    List<String> mixCommandArgs = getRunConfiguration().getMixArgs();
    mixParams.addAll(mixCommandArgs);
    if (getRunConfiguration() instanceof MixExUnitRunConfiguration && !mixCommandArgs.contains("--trace")) {
      // Prevents tests from timing out while debugging
      mixParams.add("--trace");
    }

    GeneralCommandLine commandLine = MixRunningStateUtil.commandLine(getRunConfiguration(), elixirParams, mixParams);

    LOG.debug("Running debugger process. Command line (platform-independent): ");
    LOG.debug(commandLine.getCommandLineString());

    Process process = commandLine.createProcess();
    elixirProcessHandler = new OSProcessHandler(process, commandLine.getCommandLineString());

    LOG.debug("Debugger process started.");
    return elixirProcessHandler;
  }
}
