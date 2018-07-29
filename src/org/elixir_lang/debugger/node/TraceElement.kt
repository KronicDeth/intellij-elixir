package org.elixir_lang.debugger.node

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.beam.term.inspect

class TraceElement(
        val level: Int,
        val module: String,
        val function: String,
        val arguments: List<OtpErlangObject>,
        val bindings: List<Binding>,
        val file: String,
        val line: Int
) {
    companion object {
        // {level, {module, function, arguments}, bindings, {file, line}}
        private const val ARITY = 4
        private val LOGGER = Logger.getInstance(TraceElement::class.java)

        fun from(otpStackFrame: OtpErlangObject): TraceElement? =
            when (otpStackFrame) {
                is OtpErlangTuple -> from(otpStackFrame)
                else -> {
                    LOGGER.error("OTP Stack Frame (${inspect(otpStackFrame)}) is not an OtpErlangTuple")

                    null
                }
            }

        private fun from(otpStackFrame: OtpErlangTuple): TraceElement? {
            val arity = otpStackFrame.arity()

            return if (arity == ARITY) {
                Level.from(otpStackFrame.elementAt(0))?.let { level ->
                    ModuleFunctionArguments.from(otpStackFrame.elementAt(1))?.let { (module, function, arguments) ->
                        Bindings.from(otpStackFrame.elementAt(2))?.let { bindings ->
                            FileLine.from(otpStackFrame.elementAt(3))?.let { (file, line) ->
                                TraceElement(level, module, function, arguments, bindings, file, line)
                            }
                        }
                    }
                }
            } else {
                LOGGER.error("OTP Stack Frame (${inspect(otpStackFrame)}) arity ($arity) is not expected ($ARITY)")

                null
            }
        }
    }
}
