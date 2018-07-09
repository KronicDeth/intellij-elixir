package org.elixir_lang.debugger.node.command

import com.ericsson.otp.erlang.*
import org.elixir_lang.debugger.node.Command
import org.elixir_lang.debugger.stack_frame.variable.Elixir
import java.nio.charset.Charset

class Evaluate(
        private val pid: OtpErlangPid,
        private val stackPointer: Int,
        private val module: OtpErlangAtom,
        private val function: String,
        private val arity: Int,
        private val file: String,
        private val line: Int,
        private val expression: String
) : Command {
    override fun toMessage(): OtpErlangTuple =
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
}

private fun elixirString(kotlinString: String) =
    OtpErlangBitstr(kotlinString.toByteArray(Charset.forName("UTF-8")))

private fun otpErlangLong(int: Int) = OtpErlangLong(int.toLong())

private fun otpErlangMapOf(vararg pairs: Pair<OtpErlangObject, OtpErlangObject>): OtpErlangMap {
    val map = OtpErlangMap()

    pairs.forEach { (key, value) ->
        map.put(key, value)
    }

    return map
}

private fun List<Elixir>.mapToErlangVariables(): OtpErlangMap {
    val map = OtpErlangMap()

    forEach { elixirVariable ->
        map.put(OtpErlangAtom(elixirVariable.name), OtpErlangAtom(elixirVariable.currentBinding.name))
    }

    return map
}
