package org.elixir_lang.debugger

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangPid
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator

class Evaluator(
        private val process: Process,
        private val pid: OtpErlangPid,
        private val module: OtpErlangAtom,
        private val stackPointer: Int
) : XDebuggerEvaluator() {
    override fun evaluate(expression: String, callback: XEvaluationCallback, expressionPosition: XSourcePosition?) {
        process.evaluate(pid, module, expression, stackPointer, callback)
    }
}
