package org.elixir_lang.debugger

import com.ericsson.otp.erlang.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import org.elixir_lang.Clause
import org.elixir_lang.Server
import org.elixir_lang.beam.term.inspect
import org.elixir_lang.debugger.node.event.Listener
import org.elixir_lang.debugger.node.handle_cast.BreakpointReached
import org.elixir_lang.debugger.node.ok_error.OKError
import org.elixir_lang.debugger.node.ok_error_reason.OK
import org.elixir_lang.debugger.node.ok_error_reason.OKErrorReason
import org.elixir_lang.debugger.stack_frame.value.Factory
import org.elixir_lang.generic_server.Behaviour
import org.elixir_lang.generic_server.handleMessage
import org.elixir_lang.run
import java.nio.charset.Charset

fun otpErlangMapOf(vararg pairs: Pair<OtpErlangObject, OtpErlangObject>): OtpErlangMap {
    val map = OtpErlangMap()

    pairs.forEach { (key, value) ->
        map.put(key, value)
    }

    return map
}

fun OtpErlangList.toInterpretedModuleList(): List<InterpretedModule> =
        withIndex()
                .mapNotNull { (index, element) -> interpretedModuleFrom(index, element) }

class Node(
        debuggerNodeName: String,
        debuggedNodeName: String,
        cookie: String,
        ensureAllStarted: (() -> Unit),
        private val eventListener: Listener
) : Behaviour {
    override fun handleCall(from: OtpErlangTuple, request: OtpErlangObject): OtpErlangObject {
        LOGGER.error("Unexpected ${javaClass.canonicalName}.handleCall(${inspect(from)}, ${inspect(request)})")

        return OtpErlangAtom("error")
    }

    override fun handleCast(request: OtpErlangObject): OtpErlangObject =
        handleCastClauses.run(OtpErlangList(arrayOf(request)))

    private val handleCastClauses: List<Clause> = listOf<Clause>(
            BreakpointReached(this, eventListener)
    )

    private val local = Server("Elixir.IntelliJElixir.Debugger.Client", debuggerNodeName)
    private val remote = Server("Elixir.IntelliJElixir.Debugger.Server", debuggedNodeName)
    // MUST be created on construction, so that it isn't initialized in multiple threads
    private val mailBox = local.mailBox(remote, cookie, ensureAllStarted)

    init {
        runDebugger()
    }

    private var myLastSuspendedPid: OtpErlangPid? = null

    fun attach(): org.elixir_lang.debugger.node.ok_error.OK =
            OtpErlangAtom("attach")
                    .let { callDebugged(it) }
                    .let { OKError.from(it) }
                    .let { it as org.elixir_lang.debugger.node.ok_error.OK }

    fun stop() {
        mailBox.close()
    }

    fun processSuspended(pid: OtpErlangPid) {
        myLastSuspendedPid = pid
    }

    fun setBreakpoint(module: OtpErlangAtom, line: Int): OKErrorReason =
            setBreakpointRequest(module, line)
                    .let { callDebugged(it) }
                    .let { OKErrorReason.from(it) }!!

    fun removeBreakpoint(module: OtpErlangAtom, line: Int): OK =
            removeBreakpointRequest(module, line)
                    .let { callDebugged(it) }
                    .let { OKErrorReason.from(it) }
                    .let { it as OK }

    private fun setBreakpointRequest(module: OtpErlangAtom, line: Int) =
            OtpErlangTuple(arrayOf(
                    OtpErlangAtom("set_breakpoint"),
                    module,
                    OtpErlangInt(line)
            ))

    private fun removeBreakpointRequest(module: OtpErlangAtom, line: Int) =
            OtpErlangTuple(arrayOf(
                    OtpErlangAtom("remove_breakpoint"),
                    module,
                    OtpErlangInt(line)
            ))

    fun interpret(module: OtpErlangAtom): OKError =
            interpretRequest(module)
                    .let { callDebugged(it) }
                    .let { OKError.from(it) }!!

    private fun interpretRequest(module: OtpErlangAtom) =
            OtpErlangTuple(arrayOf(
                    OtpErlangAtom("interpret"),
                    module
            ))

    fun interpret(sdkPaths: Iterable<String>, doNotInterpretPatterns: Iterable<String>): org.elixir_lang.debugger.node.ok_error.OK =
            interpretRequest(sdkPaths, doNotInterpretPatterns)
                    .let { callDebugged(it) }
                    .let { OKError.from(it) }
                    .let { it as org.elixir_lang.debugger.node.ok_error.OK }

    private fun interpretRequest(sdkPaths: Iterable<String>, doNotInterpretPatterns: Iterable<String>) =
            OtpErlangTuple(arrayOf(
                    OtpErlangAtom("interpret"),
                    otpErlangMapOf(
                            OtpErlangAtom("reject_elixir_module_name_patterns") to
                                    doNotInterpretPatterns.toOtpErlangList(),
                            OtpErlangAtom("sdk_paths") to
                                    sdkPaths.toOtpErlangList()
                    )
            ))

    fun interpreted(): List<InterpretedModule> =
            OtpErlangAtom("interpreted")
                    .let { callDebugged(it) }
                    .let { it as OtpErlangList }
                    .let(OtpErlangList::toInterpretedModuleList)

    fun stopInterpreting(module: OtpErlangAtom): org.elixir_lang.debugger.node.ok_error.OK =
            stopInterpretingRequest(module)
                    .let { callDebugged(it) }
                    .let { OKError.from(it) }
                    .let { it as org.elixir_lang.debugger.node.ok_error.OK }

    private fun stopInterpretingRequest(module: OtpErlangAtom) =
            OtpErlangTuple(arrayOf(
                    OtpErlangAtom("stop_interpreting"),
                    module
            ))

    fun stepInto(): org.elixir_lang.debugger.node.ok_error.OK =
            stepIntoRequest(myLastSuspendedPid!!)
                    .let { callDebugged(it) }
                    .let { OKError.from(it) }
                    .let { it as org.elixir_lang.debugger.node.ok_error.OK }

    private fun stepIntoRequest(pid: OtpErlangPid) = OtpErlangTuple(arrayOf(
            OtpErlangAtom("step_into"),
            pid
    ))

    fun stepOver(): org.elixir_lang.debugger.node.ok_error.OK =
            stepOverRequest(myLastSuspendedPid!!)
                    .let { callDebugged(it) }
                    .let { OKError.from(it) }
                    .let { it as org.elixir_lang.debugger.node.ok_error.OK }

    private fun stepOverRequest(pid: OtpErlangPid) = OtpErlangTuple(arrayOf(
            OtpErlangAtom("step_over"),
            pid
    ))

    fun stepOut(): org.elixir_lang.debugger.node.ok_error.OK =
            stepOutRequest(myLastSuspendedPid!!)
                    .let { callDebugged(it) }
                    .let { OKError.from(it) }
                    .let { it as org.elixir_lang.debugger.node.ok_error.OK }

    private fun stepOutRequest(pid: OtpErlangPid) = OtpErlangTuple(arrayOf(
            OtpErlangAtom("step_out"),
            pid
    ))

    fun resume(): org.elixir_lang.debugger.node.ok_error.OK =
            continueRequest(myLastSuspendedPid!!)
                    .let { callDebugged(it) }
                    .let { OKError.from(it) }
                    .let { it as org.elixir_lang.debugger.node.ok_error.OK }

    private fun continueRequest(pid: OtpErlangPid) = OtpErlangTuple(arrayOf(
            OtpErlangAtom("continue"),
            pid
    ))

    fun evaluate(pid: OtpErlangPid,
                 stackPointer: Int,
                 module: OtpErlangAtom,
                 function: String,
                 arity: Int,
                 file: String,
                 line: Int,
                 expression: String,
                 callback: XDebuggerEvaluator.XEvaluationCallback) {
        evaluateRequest(pid, stackPointer, module, function, arity, file, line, expression)
                .let { callDebugged(it) }
                .let { callback.evaluated(Factory.create(it)) }
    }

    private fun otpErlangLong(int: Int) = OtpErlangLong(int.toLong())

    private fun evaluateRequest(
            pid: OtpErlangPid,
            stackPointer: Int,
            module: OtpErlangAtom,
            function: String,
            arity: Int,
            file: String,
            line: Int,
            expression: String
    ) =
            OtpErlangTuple(arrayOf(
                    OtpErlangAtom("evaluate"),
                    otpErlangMapOf(
                            OtpErlangAtom("env") to otpErlangMapOf(
                                    OtpErlangAtom("file") to elixirString(file),
                                    OtpErlangAtom("function") to OtpErlangTuple(arrayOf(
                                            OtpErlangAtom(function),
                                            otpErlangLong(arity)
                                    )),
                                    OtpErlangAtom("line") to otpErlangLong(line),
                                    OtpErlangAtom("module") to module
                            ),
                            OtpErlangAtom("expression") to elixirString(expression),
                            OtpErlangAtom("pid") to pid,
                            OtpErlangAtom("stack_pointer") to otpErlangLong(stackPointer)
                    )
            ))

    private fun runDebugger() {
        ApplicationManager.getApplication().executeOnPooledThread { debugger() }
    }

    private fun debugger() {
        try {
            loop()
        } catch (otpErlangExit: OtpErlangExit) {
            eventListener.debuggerStopped()
        } catch (exception: Exception) {
            eventListener.unknownMessage(exception.message!!)
        }
    }

    private fun callDebugged(request: OtpErlangObject): OtpErlangObject =
            mailBox.genericServerCall(remote, request, TIMEOUT_IN_MILLISECONDS)

    private tailrec fun loop() {
        mailBox.receive { receivedMessage ->
            handleMessage(receivedMessage)
        }

        loop()
    }
}

private fun elixirString(kotlinString: String) =
        OtpErlangBitstr(kotlinString.toByteArray(Charset.forName("UTF-8")))

private fun Iterable<String>.toOtpErlangList(): OtpErlangList =
        map { elixirString(it) }
                .toTypedArray()
                .let { OtpErlangList(it) }

private const val TIMEOUT_IN_MILLISECONDS = 60000
private const val EXPECTED_INTERPRETED_MODULE_ARITY = 2
private const val INTERPRETED_INDEX = 0
private const val MODULE_INDEX = 1

private val LOGGER = Logger.getInstance("org.elixir_lang.debugger")

private fun interpretedModuleFrom(index: Int, element: OtpErlangObject): InterpretedModule? =
        when (element) {
            is OtpErlangTuple -> interpretedModuleFrom(index, element)
            else -> {
                LOGGER.error("Index ($index) is not a tuple in list at index 1 in `:interpreted` message")

                null
            }
        }

private fun interpretedModuleFrom(index: Int, tuple: OtpErlangTuple): InterpretedModule? {
    val arity = tuple.arity()

    return if (arity == EXPECTED_INTERPRETED_MODULE_ARITY) {
        val interpretedTerm = tuple.elementAt(INTERPRETED_INDEX)

        if (interpretedTerm is OtpErlangAtom) {
            val interpreted = interpretedTerm.booleanValue()
            val moduleTerm = tuple.elementAt(MODULE_INDEX)

            if (moduleTerm is OtpErlangAtom) {
                InterpretedModule(interpreted, moduleTerm)
            } else {
                LOGGER.error("Index ($index) tuple index ($MODULE_INDEX) element (${inspect(moduleTerm)}) is not a module")

                null
            }
        } else {
            LOGGER.error("Index ($index) tuple index ($INTERPRETED_INDEX) element (${inspect(interpretedTerm)}) is not an atom")

            null
        }
    } else {
        LOGGER.error("Index ($index) tuple arity ($arity) differs from expected arity ($EXPECTED_INTERPRETED_MODULE_ARITY)")

        null
    }
}

