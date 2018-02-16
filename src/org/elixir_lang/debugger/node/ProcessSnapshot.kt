package org.elixir_lang.debugger.node

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangPid
import com.ericsson.otp.erlang.OtpErlangTuple
import com.intellij.openapi.diagnostic.Logger

class ProcessSnapshot(val pid: OtpErlangPid, val stack: List<TraceElement>) {
    val pidString: String
        get() = pid.toString()

    companion object {
        private val LOGGER = Logger.getInstance(ProcessSnapshot::class.java)

        fun from(indexedTerm: IndexedValue<OtpErlangObject>): ProcessSnapshot? =
                from(indexedTerm.value, indexedTerm.index)

        private fun from(term: OtpErlangObject, index: Int): ProcessSnapshot? =
                if (term is OtpErlangTuple) {
                    from(term, index)

                } else {
                    LOGGER.error("Element at index $index is not an OtpErlangTuple")

                    null
                }

        private fun from(tuple: OtpErlangTuple, index: Int): ProcessSnapshot? {
            val elementArity = tuple.arity()

            return if (elementArity == 5) {
                val pid = tuple.elementAt(0)

                if (pid is OtpErlangPid) {
                    val otpStack = tuple.elementAt(4)

                    if (otpStack is OtpErlangList) {
                        val stack = otpStackToDebuggerStack(otpStack)

                        ProcessSnapshot(pid, stack)
                    } else {
                        LOGGER.error("Element at index 4 is not an OtpErlangList")

                        null
                    }
                } else {
                    LOGGER.error("Element at index 0 is not an OtpErlangPid")

                    null
                }
            } else {
                LOGGER.error("Tuple arity ($elementArity) is not 5")

                null
            }
        }

        private fun otpStackToDebuggerStack(otpStack: OtpErlangList): List<TraceElement> =
                otpStack.mapNotNull { otpStackFrame ->
                    TraceElement.from(otpStackFrame)
                }

    }
}
