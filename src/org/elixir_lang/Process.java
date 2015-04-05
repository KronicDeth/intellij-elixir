package org.elixir_lang;

import com.ericsson.otp.erlang.*;

/**
 * Emulates the Process module in Elixir
 */
public class Process {
    public static OtpErlangPid whereis(OtpMbox localMbox, OtpNode localNode, String remoteName, String remoteNode, int timeout) throws OtpErlangExit, OtpErlangDecodeException {
        return (OtpErlangPid) RPC.unmonitoredCall(
                remoteNode,
                localMbox,
                localNode,
                timeout,
                new OtpErlangAtom("Elixir.Process"),
                new OtpErlangAtom("whereis"),
                new OtpErlangAtom(remoteName)
        );
    }
}
