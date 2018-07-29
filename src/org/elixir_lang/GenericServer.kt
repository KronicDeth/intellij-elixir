package org.elixir_lang

import com.ericsson.otp.erlang.*

/**
 * Emulates GenServer
 */
object GenericServer {
    @Throws(OtpErlangExit::class, OtpErlangDecodeException::class)
    fun call(
            callerMbox: OtpMbox,
            callerNode: OtpNode,
            serverName: String,
            serverNode: String,
            request: OtpErlangObject,
            timeout: Int
    ): OtpErlangObject? {
        val serverPid = Process.whereis(callerMbox, callerNode, serverName, serverNode, timeout)
                ?: throw OtpErlangExit("Could not determine PID for $serverName on $serverNode within $timeout")

        return call(callerMbox, callerNode, serverPid, request, timeout)
    }

    @Throws(OtpErlangExit::class, OtpErlangDecodeException::class)
    private fun call(
            callerMbox: OtpMbox,
            callerNode: OtpNode,
            serverPid: OtpErlangPid,
            request: OtpErlangObject,
            timeout: Int
    ): OtpErlangObject? {
        callerMbox.link(serverPid)

        val response = unmonitoredCall(callerMbox, callerNode, serverPid, request, timeout)

        callerMbox.unlink(serverPid)

        return response
    }

    @Throws(OtpErlangExit::class, OtpErlangDecodeException::class)
    private fun unmonitoredCall(
            callerMbox: OtpMbox,
            callerNode: OtpNode,
            serverPid: OtpErlangPid,
            request: OtpErlangObject,
            timeout: Int
    ): OtpErlangObject? =
            Generic.unmonitoredCall(
                    callerMbox,
                    callerNode,
                    serverPid,
                    GEN_CALL,
                    request,
                    timeout
            )

    @Throws(OtpErlangExit::class, OtpErlangDecodeException::class)
    fun unmonitoredCall(
            callerMbox: OtpMbox,
            callerNode: OtpNode,
            serverName: String,
            serverNode: String,
            request: OtpErlangObject,
            timeout: Int
    ): OtpErlangObject? =
            Generic.unmonitoredCall(
                    callerMbox,
                    callerNode,
                    serverName,
                    serverNode,
                    GEN_CALL,
                    request,
                    timeout
            )

    val GEN_CALL = OtpErlangAtom("\$gen_call")
    val GEN_CAST = OtpErlangAtom("\$gen_cast")
}
