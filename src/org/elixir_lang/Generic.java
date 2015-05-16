package org.elixir_lang;

import com.ericsson.otp.erlang.*;
import org.apache.commons.lang.NotImplementedException;

/**
 * Emulates behavior of the `gen` Module in Erlang
 */
public class Generic {
    /**
     * Sends a generic message containing {@code request} with the given {@code label} to {@code remoteName} process on
     * {@code remoteNode} from {@code localMbox} on {@code localNode}.  Unlike, `gen:call`, a monitor is not setup prior
     * to the call.
     *
     * @link https://github.com/erlang/otp/blob/OTP_R16B03-1/lib/stdlib/src/gen.erl#L209
     */
    public static OtpErlangObject unmonitoredCall(final OtpMbox localMbox, final OtpNode localNode, final String remoteName, final String remoteNode, final OtpErlangAtom label, final OtpErlangObject request, final int timeout) throws OtpErlangExit, OtpErlangDecodeException {
        return unmonitoredCall(
                localMbox,
                localNode,
                new Sender() {
                    @Override
                    public void send(OtpErlangObject message) {
                        localMbox.send(remoteName, remoteNode, message);
                    }
                },
                label,
                request,
                timeout
        );
    }

    public static OtpErlangObject unmonitoredCall(final OtpMbox localMbox, final OtpNode localNode, final OtpErlangPid remotePid, final OtpErlangAtom label, final OtpErlangObject request, final int timeout) throws OtpErlangExit, OtpErlangDecodeException {
        return unmonitoredCall(
                localMbox,
                localNode,
                new Sender() {
                    @Override
                    public void send(OtpErlangObject message) {
                        localMbox.send(remotePid, message);
                    }
                },
                label,
                request,
                timeout
        );
    }

    /*
     * Private
     */

    private static OtpErlangTuple returnAddress(OtpMbox otpMbox, OtpErlangRef otpErlangRef) {
        return new OtpErlangTuple(
                new OtpErlangObject[]{
                        otpMbox.self(),
                        otpErlangRef
                }
        );
    }

    private static OtpErlangObject unmonitoredCall(final OtpMbox localMbox, final OtpNode localNode, final Sender sender, final OtpErlangAtom label, final OtpErlangObject request, final int timeout) throws OtpErlangExit, OtpErlangDecodeException {
                OtpErlangRef ref = localNode.createRef();

        OtpErlangObject message = new OtpErlangTuple(
                new OtpErlangObject[]{
                        label,
                        returnAddress(localMbox, ref),
                        request
                }
        );

        sender.send(message);

        OtpErlangObject received = localMbox.receive(timeout);
        OtpErlangObject response = null;

        if (received != null) {
            OtpErlangTuple tuple = (OtpErlangTuple) received;
            OtpErlangObject first = tuple.elementAt(0);

            if (!first.equals(ref)) {
                throw new NotImplementedException("Expected ref " + ref + ", but received " + first);
            }

            response = tuple.elementAt(1);
        }

        return response;
    }
}
