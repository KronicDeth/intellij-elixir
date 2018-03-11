package org.elixir_lang;

import com.ericsson.otp.erlang.*;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import static org.elixir_lang.psi.impl.QuotableImpl.NIL;

/**
 * Emulates the Process module in Elixir
 */
public class Process {
    @Nullable
    public static OtpErlangPid whereis(OtpMbox localMbox, OtpNode localNode, String remoteName, String remoteNode, int timeout) throws OtpErlangExit, OtpErlangDecodeException {
        OtpErlangObject response = RPC.unmonitoredCall(
                remoteNode,
                localMbox,
                localNode,
                timeout,
                new OtpErlangAtom("Elixir.Process"),
                new OtpErlangAtom("whereis"),
                new OtpErlangAtom(remoteName)
        );

        OtpErlangPid pid;

        if (response instanceof OtpErlangAtom) {
            if (response.equals(NIL)) {
                pid = null;
            } else {
                throw new NotImplementedException("Expected atoms to be nil, but got" + response);
            }
        } else {
            pid = (OtpErlangPid) response;
        }

        return pid;
    }
}
