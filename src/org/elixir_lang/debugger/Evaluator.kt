package org.elixir_lang.debugger

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangPid
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator

class Evaluator(
        private val process: Process,
        private val pid: OtpErlangPid,
        private val stackPointer: Int,
        private val module: OtpErlangAtom,
        private val function: String,
        private val arity: Int,
        private val file: String,
        private val line: Int
) : XDebuggerEvaluator() {
    override fun evaluate(expression: String, callback: XEvaluationCallback, expressionPosition: XSourcePosition?) {
        process.evaluate(pid, stackPointer, module, function, arity, file, line, expression, callback)
    }
}
