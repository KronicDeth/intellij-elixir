package org.elixir_lang;

import com.ericsson.otp.erlang.*;

/**
 * Emulates `rpc` Erlang module
 */
public class RPC {
    /**
     * Like `rpc:call`, but not monitoring of the remote node occurs.
     */
    public static OtpErlangObject unmonitoredCall(String remoteNode, OtpMbox localMbox, OtpNode localNode, int timeout, OtpErlangAtom module, OtpErlangAtom function, OtpErlangObject... arguments) throws OtpErlangExit, OtpErlangDecodeException {
        OtpErlangObject request = new OtpErlangTuple(
                new OtpErlangObject[]{
                        new OtpErlangAtom("call"),
                        module,
                        function,
                        new OtpErlangList(
                                arguments
                        ),
                        localMbox.self()
                }
        );

        return GenericServer.unmonitoredCall(localMbox, localNode, "rex", remoteNode, request, timeout);
    }
}
