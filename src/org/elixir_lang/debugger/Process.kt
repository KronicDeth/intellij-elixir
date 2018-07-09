/*
 * Copyright 2012-2015 Sergey Ignatov
 * Copyright 2017 Jake Becker
 * Copyright 2017-2018 Luke Imhoff
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

package org.elixir_lang.debugger

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangPid
import com.intellij.execution.ExecutionException
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.ui.ExecutionConsole
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.testFramework.LightVirtualFile
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.evaluation.EvaluationMode
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import org.elixir_lang.ElixirFileType
import org.elixir_lang.beam.chunk.lines.file_names.Index
import org.elixir_lang.beam.term.inspect
import org.elixir_lang.debugger.configuration.Debuggable
import org.elixir_lang.debugger.line_breakpoint.Handler
import org.elixir_lang.debugger.line_breakpoint.Properties
import org.elixir_lang.debugger.node.Exception
import org.elixir_lang.debugger.node.ProcessSnapshot
import org.elixir_lang.debugger.node.event.Listener
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.psi.impl.getModuleName
import org.elixir_lang.run.ensureWorkingDirectory
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class Process(session: XDebugSession, private val executionEnvironment: ExecutionEnvironment) :
        XDebugProcess(session), Listener {
    init {
        session.setPauseActionSupported(false)
    }

    override fun createConsole(): ExecutionConsole = debuggedExecutionResult.executionConsole
    override fun createTabLayouter(): TabLayouter = tabLayouter

    private val tabLayouter by lazy {
        TabLayouter(node, debuggerExecutionResult.executionConsole)
    }

    private fun sourcePosition(breakpoint: XLineBreakpoint<Properties>): SourcePosition? =
        breakpoint.sourcePosition?.let { SourcePosition.create(it) }

    private val breakpointHandlers = arrayOf<XBreakpointHandler<*>>(Handler(this))

    private val debuggerExecutionResult by lazy {
        executionEnvironment.executor.let { executor ->
            debuggableConfiguration
                    .debuggerConfiguration(debuggerName, erlConfigPath, node.localDebuggerPort)
                    .getState(executor, executionEnvironment)
                    .execute(executor, executionEnvironment.runner)!!
        }
    }

    private val debuggedExecutionResult by lazy {
        executionEnvironment.executor.let { executor ->
            debuggableConfiguration
                    .debuggedConfiguration(debuggedName, erlConfigPath)
                    .getState(executor, executionEnvironment)!!
                    .execute(executor, executionEnvironment.runner)!!
        }
    }

    private val nodesUUID by lazy { UUID.randomUUID()!! }
    private val debuggedName by lazy { "debugged$nodesUUID@127.0.0.1" }
    private val debuggerName by lazy { "debugger$nodesUUID@127.0.0.1" }

    private val erlConfig by lazy {
        """
        |[{kernel,
        |  [
        |    {sync_nodes_mandatory, ['$debuggedName', '$debuggerName']},
        |    {sync_nodes_timeout, 10000}
        |  ]}
        |].
        """.trimMargin()
    }

    private val erlConfigPath by lazy {
        val temporaryDirectory = FileUtil.createTempDirectory("intellij_elixir", null)
        val file = File(temporaryDirectory, "sys.config")
        file.writeText(erlConfig)

        file.path
    }

    private val node by lazy {
        try {
            //TODO add the debugger node to disposable hierarchy (we may fail to initialize session so the session will not be stopped!)
            Node(this)
        } catch (e: Exception) {
            throw ExecutionException(e)
        }
    }

    private val debuggableConfiguration: Debuggable<*>
        get() = session.runProfile as Debuggable<*>

    private val sourcePositionToBreakpoint = ConcurrentHashMap<SourcePosition, XLineBreakpoint<Properties>>()

    fun addBreakpoint(breakpoint: XLineBreakpoint<Properties>) {
        sourcePosition(breakpoint)?.let { sourcePosition ->
            val moduleNameSet = moduleNameSet(sourcePosition)

            if (!moduleNameSet.isEmpty()) {
                sourcePositionToBreakpoint[sourcePosition] = breakpoint

                for (moduleName in moduleNameSet) {
                    node.setBreakpoint(moduleName, sourcePosition.file.path, sourcePosition.line)
                }
            } else {
                session.reportMessage(
                        "Unable to determine module for breakpoint at ${sourcePosition.file} line ${sourcePosition.line}",
                        MessageType.ERROR
                )
            }
        }
    }

    override fun breakpointIsSet(module: String, file: String, line: Int) {}

    override fun breakpointReached(pid: OtpErlangPid, snapshots: List<ProcessSnapshot>) {
        val processInBreakpoint = ContainerUtil.find(snapshots) { elixirProcessSnapshot -> elixirProcessSnapshot.pid == pid }!!
        val breakPosition = SourcePosition.create(processInBreakpoint)
        val breakpoint = getLineBreakpoint(breakPosition)
        val suspendContext = SuspendContext(this, pid, snapshots)
        if (breakpoint == null) {
            session.positionReached(suspendContext)
        } else {
            val shouldSuspend = session.breakpointReached(breakpoint, null, suspendContext)
            if (!shouldSuspend) {
                resume()
            }
        }
    }

    override fun debuggerStarted() {
        session.reportMessage("Debug process started", MessageType.INFO)
    }

    override fun debuggerStopped() {
        session.reportMessage("Debug process stopped", MessageType.INFO)
        session.stop()
    }

    override fun doGetProcessHandler(): ProcessHandler = debuggedExecutionResult.processHandler

    override fun failedToDebugRemoteNode(nodeName: String, error: OtpErlangObject) {
        session.reportMessage(
                "Failed to debug remote node '$nodeName'. Details: ${inspect(error)}",
                MessageType.ERROR
        )
    }

    override fun failedToInterpretModules(nodeName: String,
                                          errorReasonByModule: Map<String, OtpErlangObject>) {
        val stringBuilder = StringBuilder("Failed to interpret modules on node (")
                .append(nodeName)
                .append("):\n\n")

        for ((key, value) in errorReasonByModule) {
            stringBuilder.append(key).append(": ").append(inspect(value)).append("\n\n")
        }

        stringBuilder.append(
                "Make sure they are compiled with debug_info option, their sources are located in same directory as " + ".beam files, modules are available on the node."
        )

        session.reportMessage(stringBuilder.toString(), MessageType.WARNING)
    }

    override fun failedToSetBreakpoint(module: String,
                                       file: String,
                                       line: Int,
                                       errorMessage: OtpErlangObject) {
        val sourcePosition = SourcePosition.create(file, line)
        val breakpoint = getLineBreakpoint(sourcePosition)

        if (breakpoint != null) {
            session.updateBreakpointPresentation(breakpoint, AllIcons.Debugger.Db_invalid_breakpoint, inspect(errorMessage))
        }

        session.reportMessage("Failed to set breakpoint. Module: " + module + " Line: " + (line + 1), MessageType.WARNING)
    }

    override fun interpretedModules(interpretedModuleList: List<InterpretedModule>) {
        tabLayouter.interpretedModules(interpretedModuleList)
    }

    override fun getBreakpointHandlers(): Array<XBreakpointHandler<*>> = breakpointHandlers

    override fun getEditorsProvider(): XDebuggerEditorsProvider {
        return object : XDebuggerEditorsProvider() {
            override fun createDocument(project: Project,
                                        text: String,
                                        sourcePosition: XSourcePosition?,
                                        mode: EvaluationMode): Document {
                val file = LightVirtualFile("plain-text-elixir-debugger.txt", text)

                return FileDocumentManager.getInstance().getDocument(file)!!
            }

            override fun getFileType(): FileType {
                return ElixirFileType.INSTANCE
            }
        }
    }

    private fun getLineBreakpoint(sourcePosition: SourcePosition?): XLineBreakpoint<Properties>? =
            if (sourcePosition != null) sourcePositionToBreakpoint[sourcePosition] else null

    private fun moduleNameSet(breakpointPosition: SourcePosition): Set<String> {
        val virtualFile = breakpointPosition.file
        val project = debuggableConfiguration.getProject()

        return PsiManager
                .getInstance(project)
                .findFile(virtualFile)
                ?.let { psiFile ->
            val element = psiFile.findElementAt(breakpointPosition.sourcePosition.offset)

            when (psiFile) {
                is ElixirFile -> // TODO allow multiple module names for `defimpl`
                    element?.getModuleName()?.let { setOf(it) } ?: emptySet()
                is org.elixir_lang.eex.File -> {
                    val module = ModuleUtilCore.findModuleForPsiElement(psiFile)
                    val rootDirectory = ensureWorkingDirectory(project, module)

                    val path = virtualFile.path
                    try {
                        java.io.File(path).relativeTo(
                                java.io.File(rootDirectory)
                        )
                    } catch (illegalArgumentException: IllegalArgumentException) {
                        null
                    }?.let { relativeFile ->
                        val filename = relativeFile.path

                        FileBasedIndex
                                .getInstance()
                                .getContainingFiles(
                                        Index.NAME,
                                        filename,
                                        GlobalSearchScope.allScope(project)
                                )
                                .map { fileNameVirtualFile ->
                                    fileNameVirtualFile.name.removePrefix("Elixir.").removeSuffix(".beam")
                                }
                                .toSet()
                    } ?:
                    emptySet()
                }
                else -> emptySet()
            }
        } ?:
        emptySet()
    }

    fun removeBreakpoint(breakpoint: XLineBreakpoint<Properties>) {
       sourcePosition(breakpoint)?.let { breakpointPosition ->
           sourcePositionToBreakpoint.remove(breakpointPosition)

           moduleNameSet(breakpointPosition).forEach { moduleName ->
               node.removeBreakpoint(moduleName, breakpointPosition.line)
           }
       }
    }

    override fun resume() {
        node.resume()
    }

    override fun runToPosition(position: XSourcePosition) {
        //TODO implement me
    }

    override fun sessionInitialized() {
        // lazy, so starts node
        node

        val debuggerProcessHandler = debuggerExecutionResult.processHandler
        debuggerProcessHandler.addProcessListener(object : ProcessListener {
            override fun startNotified(event: ProcessEvent) {}

            override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                session.reportMessage(event.text, MessageType.WARNING)
            }

            override fun processWillTerminate(event: ProcessEvent, willBeDestroyed: Boolean) {}

            override fun processTerminated(event: ProcessEvent) {
                event.text?.let { text ->
                    session.reportMessage(text, MessageType.INFO)
                }

                session.reportMessage("Debugger node terminated", MessageType.INFO)

                debuggedExecutionResult.processHandler.destroyProcess()

                session.stop()
            }
        })

        node.connect(debuggedName)
        node.interpreted()
    }

    override fun startStepInto() {
        node.stepInto()
    }

    override fun startStepOut() {
        node.stepOut()
    }

    override fun startStepOver() {
        node.stepOver()
    }

    override fun stop() {
        node.stop()
        debuggerExecutionResult.processHandler.destroyProcess()
        debuggedExecutionResult.processHandler.destroyProcess()
    }

    override fun unknownMessage(messageText: String) {
        session.reportMessage("Unknown message received: $messageText", MessageType.WARNING)
    }

    fun evaluate(
            pid: OtpErlangPid,
            stackPointer: Int,
            module: OtpErlangAtom,
            function: String,
            arity: Int,
            file: String,
            line: Int,
            expression: String,
            callback: XDebuggerEvaluator.XEvaluationCallback
    ) {
        node.evaluate(pid, stackPointer, module, function, arity, file, line, expression, callback)
    }
}
