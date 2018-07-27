package org.elixir_lang

import com.ericsson.otp.erlang.*
import org.apache.commons.lang.NotImplementedException

/**
 * Emulates behavior of the `gen` Module in Erlang
 */
object Generic {
    /**
     * Sends a generic message containing `request` with the given `label` to `remoteName` process on
     * `remoteNode` from `localMbox` on `localNode`.  Unlike, `gen:call`, a monitor is not setup prior
     * to the call.
     *
     * @link https://github.com/erlang/otp/blob/OTP_R16B03-1/lib/stdlib/src/gen.erl#L209
     */
    @Throws(OtpErlangExit::class, OtpErlangDecodeException::class)
    fun unmonitoredCall(
            localMbox: OtpMbox,
            localNode: OtpNode,
            remoteName: String,
            remoteNode: String,
            label: OtpErlangAtom,
            request: OtpErlangObject,
            timeout: Int
    ): OtpErlangObject? =
            unmonitoredCall(
                    localMbox,
                    localNode,
                    Sender { message -> localMbox.send(remoteName, remoteNode, message) },
                    label,
                    request,
                    timeout
            )

    @Throws(OtpErlangExit::class, OtpErlangDecodeException::class)
    fun unmonitoredCall(
            localMbox: OtpMbox,
            localNode: OtpNode,
            remotePid: OtpErlangPid,
            label: OtpErlangAtom,
            request: OtpErlangObject,
            timeout: Int
    ): OtpErlangObject? =
            unmonitoredCall(
                    localMbox,
                    localNode,
                    Sender { message -> localMbox.send(remotePid, message) },
                    label,
                    request,
                    timeout
            )

    /*
     * Private
     */

    private fun returnAddress(otpMbox: OtpMbox, otpErlangRef: OtpErlangRef): OtpErlangTuple = OtpErlangTuple(arrayOf(
            otpMbox.self(),
            otpErlangRef
    ))

    @Throws(OtpErlangExit::class, OtpErlangDecodeException::class)
    private fun unmonitoredCall(
            localMbox: OtpMbox,
            localNode: OtpNode,
            sender: Sender,
            label: OtpErlangAtom,
            request: OtpErlangObject,
            timeout: Int
    ): OtpErlangObject? {
        val ref = localNode.createRef()

        val message = OtpErlangTuple(arrayOf(
                label,
                returnAddress(localMbox, ref), request
        ))

        sender.send(message)

        val received = localMbox.receive(timeout.toLong())
        var response: OtpErlangObject? = null

        if (received != null) {
            val tuple = received as OtpErlangTuple
            val first = tuple.elementAt(0)

            if (first != ref) {
                throw NotImplementedException("Expected ref $ref, but received $first")
            }

            response = tuple.elementAt(1)
        }

        return response
    }
}
