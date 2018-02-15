/*
 * Copyright 2012-2015 Sergey Ignatov
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

import com.ericsson.otp.erlang.OtpErlangPid;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ColoredProcessHandler;
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
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.evaluation.EvaluationMode;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import org.elixir_lang.ElixirFileType;
import org.elixir_lang.debugger.Modules;
import org.elixir_lang.debugger.node.event.Listener;
import org.elixir_lang.debugger.Node;
import org.elixir_lang.debugger.node.Exception;
import org.elixir_lang.debugger.node.ProcessSnapshot;
import org.elixir_lang.jps.builder.ParametersList;
import org.elixir_lang.mix.runner.MixRunConfigurationBase;
import org.elixir_lang.mix.runner.MixRunningState;
import org.elixir_lang.mix.runner.MixRunningStateUtil;
import org.elixir_lang.mix.runner.exunit.MixExUnitRunConfiguration;
import org.elixir_lang.psi.ElixirFile;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.elixir_lang.debugger.DebuggerLog.LOG;

class XDebugProcess extends com.intellij.xdebugger.XDebugProcess implements Listener {
    @NotNull
    private final XBreakpointHandler<?>[] myBreakpointHandlers = new XBreakpointHandler[]{new LineBreakpointHandler(this)};
    @NotNull
    private final Node myNode;
    @NotNull
    private final OSProcessHandler myElixirProcessHandler;
    private final ExecutionEnvironment myExecutionEnvironment;
    @NotNull
    private final ConcurrentHashMap<SourcePosition, XLineBreakpoint<LineBreakpointProperties>> myPositionToLineBreakpointMap =
            new ConcurrentHashMap<>();
    @NotNull
    private final MixRunningState myRunningState;

    XDebugProcess(@NotNull XDebugSession session, ExecutionEnvironment env) throws ExecutionException {
        super(session);

        session.setPauseActionSupported(false);

        myExecutionEnvironment = env;
        myRunningState = (MixRunningState) getRunConfiguration().getState(myExecutionEnvironment.getExecutor(), myExecutionEnvironment);

        try {
            //TODO add the debugger node to disposable hierarchy (we may fail to initialize session so the session will not be stopped!)
            myNode = new Node(this);
        } catch (Exception e) {
            throw new ExecutionException(e);
        }

        //TODO split running debug target and debugger process spawning
        myElixirProcessHandler = runDebugTarget();
    }

    @NotNull
    private static ParametersList addRequires(@NotNull ParametersList elixirParametersList) throws ExecutionException {
        ParametersList updated;

        try {
            updated = Modules.add(elixirParametersList);
        } catch (IOException e) {
            throw new ExecutionException("Failed to setup debugger environment", e);
        }

        return updated;
    }

    @Nullable
    private static SourcePosition getElixirSourcePosition(@NotNull XLineBreakpoint<LineBreakpointProperties> breakpoint) {
        XSourcePosition sourcePosition = breakpoint.getSourcePosition();

        return sourcePosition != null ? SourcePosition.create(sourcePosition) : null;
    }

    void addBreakpoint(@NotNull XLineBreakpoint<LineBreakpointProperties> breakpoint) {
        SourcePosition breakpointPosition = getElixirSourcePosition(breakpoint);
        if (breakpointPosition == null) {
            return;
        }
        String moduleName = getModuleName(breakpointPosition);
        if (moduleName != null) {
            myPositionToLineBreakpointMap.put(breakpointPosition, breakpoint);
            myNode.setBreakpoint(moduleName, breakpointPosition.getFile().getPath(), breakpointPosition.getLine());
        } else {
            final String message =
                    "Unable to determine module for breakpoint at " +
                            breakpointPosition.getFile() + " line " + breakpointPosition.getLine();
            getSession().reportMessage(message, MessageType.ERROR);
        }
    }

    @Override
    public void breakpointIsSet(String module, String file, int line) {
    }

    @Override
    public void breakpointReached(@NotNull final OtpErlangPid pid, @NotNull List<ProcessSnapshot> snapshots) {
        ProcessSnapshot processInBreakpoint = ContainerUtil.find(snapshots, elixirProcessSnapshot -> elixirProcessSnapshot.getPid().equals(pid));
        assert processInBreakpoint != null;
        SourcePosition breakPosition = SourcePosition.create(processInBreakpoint);
        XLineBreakpoint<LineBreakpointProperties> breakpoint = getLineBreakpoint(breakPosition);
        SuspendContext suspendContext = new SuspendContext(pid, snapshots);
        if (breakpoint == null) {
            getSession().positionReached(suspendContext);
        } else {
            boolean shouldSuspend = getSession().breakpointReached(breakpoint, null, suspendContext);
            if (!shouldSuspend) {
                resume();
            }
        }
    }

    @NotNull
    @Override
    public ExecutionConsole createConsole() {
        ConsoleView consoleView = myRunningState.createConsoleView(myExecutionEnvironment.getExecutor());
        consoleView.attachToProcess(getProcessHandler());
        myElixirProcessHandler.startNotify();
        return consoleView;
    }

    @Override
    public void debuggerStarted() {
        getSession().reportMessage("Debug process started", MessageType.INFO);
    }

    @Override
    public void debuggerStopped() {
        getSession().reportMessage("Debug process stopped", MessageType.INFO);
        getSession().stop();
    }

    @Nullable
    @Override
    protected ProcessHandler doGetProcessHandler() {
        return myElixirProcessHandler;
    }

    @Override
    public void failedToDebugRemoteNode(String nodeName, String error) {
        String message = "Failed to debug remote node '" + nodeName + "'. Details: " + error;
        getSession().reportMessage(message, MessageType.ERROR);
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
    public void failedToSetBreakpoint(String module, @NotNull String file, int line, String errorMessage) {
        SourcePosition sourcePosition = SourcePosition.create(file, line);
        XLineBreakpoint<LineBreakpointProperties> breakpoint = getLineBreakpoint(sourcePosition);
        if (breakpoint != null) {
            getSession().updateBreakpointPresentation(breakpoint, AllIcons.Debugger.Db_invalid_breakpoint, errorMessage);
        }
        getSession().reportMessage("Failed to set breakpoint. Module: " + module + " Line: " + (line + 1), MessageType.WARNING);
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
            public Document createDocument(@NotNull Project project,
                                           @NotNull String text,
                                           @Nullable XSourcePosition sourcePosition,
                                           @NotNull EvaluationMode mode) {
                LightVirtualFile file = new LightVirtualFile("plain-text-elixir-debugger.txt", text);
                //noinspection ConstantConditions
                return FileDocumentManager.getInstance().getDocument(file);
            }

            @NotNull
            @Override
            public FileType getFileType() {
                return ElixirFileType.INSTANCE;
            }
        };
    }

    @Nullable
    private XLineBreakpoint<LineBreakpointProperties> getLineBreakpoint(@Nullable SourcePosition sourcePosition) {
        return sourcePosition != null ? myPositionToLineBreakpointMap.get(sourcePosition) : null;
    }

    @Nullable
    private String getModuleName(@NotNull SourcePosition breakpointPosition) {
        ElixirFile psiFile = (ElixirFile) PsiManager.getInstance(getRunConfiguration().getProject()).findFile(breakpointPosition.getFile());
        if (psiFile == null) {
            return null;
        }

        PsiElement elem = psiFile.findElementAt(breakpointPosition.getSourcePosition().getOffset());
        return ElixirPsiImplUtil.getModuleName(elem);
    }

    @NotNull
    private MixRunConfigurationBase getRunConfiguration() {
        MixRunConfigurationBase runConfig = (MixRunConfigurationBase) getSession().getRunProfile();
        assert runConfig != null;
        return runConfig;
    }

    void removeBreakpoint(@NotNull XLineBreakpoint<LineBreakpointProperties> breakpoint,
                          @SuppressWarnings("UnusedParameters") boolean temporary) {
        SourcePosition breakpointPosition = getElixirSourcePosition(breakpoint);
        if (breakpointPosition == null) {
            return;
        }
        myPositionToLineBreakpointMap.remove(breakpointPosition);
        String moduleName = getModuleName(breakpointPosition);
        if (moduleName != null) {
            myNode.removeBreakpoint(moduleName, breakpointPosition.getLine());
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void resume() {
        myNode.resume();
    }

    @NotNull
    private OSProcessHandler runDebugTarget() throws ExecutionException {
        OSProcessHandler elixirProcessHandler;
        LOG.debug("Preparing to run debug target.");

        RunnerAndConfigurationSettings runnerAndConfigurationSettings = myExecutionEnvironment.getRunnerAndConfigurationSettings();
        MixRunConfigurationBase runConfiguration = null;

        if (runnerAndConfigurationSettings != null) {
            runConfiguration = (MixRunConfigurationBase) runnerAndConfigurationSettings.getConfiguration();
        }

        ParametersList elixirParametersList = myRunningState.elixirParametersList(runConfiguration);
        addRequires(elixirParametersList);

        ParametersList mixParametersList = new ParametersList();
        mixParametersList.addAll(
                "intellij_elixir.debug_task",
                "--debugger-port",
                Integer.toString(myNode.getLocalDebuggerPort()),
                "--"
        );
        MixRunConfigurationBase mixRunConfigurationBase = getRunConfiguration();
        ParametersList runConfigurationMixParametersList = mixRunConfigurationBase.mixParametersList();
        mixParametersList.addAll(runConfigurationMixParametersList.getList());

        if (mixRunConfigurationBase instanceof MixExUnitRunConfiguration &&
                !runConfigurationMixParametersList.getList().contains("--trace")) {
            // Prevents tests from timing out while debugging
            mixParametersList.add("--trace");
        }

        GeneralCommandLine commandLine = MixRunningStateUtil.commandLine(mixRunConfigurationBase, elixirParametersList, mixParametersList);

        LOG.debug("Running debugger process. Command line (platform-independent): ");
        LOG.debug(commandLine.getCommandLineString());

        Process process = commandLine.createProcess();
        elixirProcessHandler = new ColoredProcessHandler(process, commandLine.getCommandLineString());

        LOG.debug("Event process started.");
        return elixirProcessHandler;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void runToPosition(@NotNull XSourcePosition position) {
        //TODO implement me
    }

    @Override
    public void sessionInitialized() {
        myNode.runTask();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void startStepInto() {
        myNode.stepInto();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void startStepOut() {
        myNode.stepOut();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void startStepOver() {
        myNode.stepOver();
    }

    @Override
    public void stop() {
        myNode.stop();
    }

    @Override
    public void unknownMessage(String messageText) {
        getSession().reportMessage("Unknown message received: " + messageText, MessageType.WARNING);
    }
}
