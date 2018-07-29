package org.elixir_lang

import com.ericsson.otp.erlang.*

/**
 * Emulates `rpc` Erlang module
 */
object RPC {
    /**
     * Like `rpc:call`, but not monitoring of the remote node occurs.
     */
    @Throws(OtpErlangExit::class, OtpErlangDecodeException::class)
    fun unmonitoredCall(
            remoteNode: String,
            localMbox: OtpMbox,
            localNode: OtpNode,
            timeout: Int,
            module: OtpErlangAtom,
            function: OtpErlangAtom,
            vararg arguments: OtpErlangObject
    ): OtpErlangObject? {
        val request = OtpErlangTuple(arrayOf(
                OtpErlangAtom("call"),
                module,
                function,
                OtpErlangList(arguments),
                localMbox.self()
        ))

        return GenericServer.unmonitoredCall(localMbox, localNode, "rex", remoteNode, request, timeout)
    }
}
