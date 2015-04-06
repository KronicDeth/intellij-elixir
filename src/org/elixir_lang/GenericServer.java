package org.elixir_lang;

import com.ericsson.otp.erlang.*;
import org.jetbrains.annotations.NotNull;

/**
 * Emulates GenServer
 */
public class GenericServer {
    public static final OtpErlangAtom GEN_CALL = new OtpErlangAtom("$gen_call");

    public static OtpErlangObject call(OtpMbox callerMbox, OtpNode callerNode, String serverName, String serverNode, OtpErlangObject request, int timeout) throws OtpErlangExit, OtpErlangDecodeException {
        OtpErlangPid serverPid = Process.whereis(callerMbox, callerNode, serverName, serverNode, timeout);

        if (serverPid == null) {
            throw new OtpErlangExit("no process");
        }

        return call(callerMbox,  callerNode, serverPid, request, timeout);
    }

    private static OtpErlangObject call(@NotNull OtpMbox callerMbox, @NotNull OtpNode callerNode, @NotNull OtpErlangPid serverPid, @NotNull OtpErlangObject request, int timeout) throws OtpErlangExit, OtpErlangDecodeException {
        callerMbox.link(serverPid);

        OtpErlangObject response = unmonitoredCall(callerMbox, callerNode, serverPid, request, timeout);

        callerMbox.unlink(serverPid);

        return response;
    }

    private static OtpErlangObject unmonitoredCall(OtpMbox callerMbox, OtpNode callerNode, OtpErlangPid serverPid, OtpErlangObject request, int timeout) throws OtpErlangExit, OtpErlangDecodeException {
        return Generic.unmonitoredCall(
                callerMbox,
                callerNode,
                serverPid,
                GEN_CALL,
                request,
                timeout
        );
    }

    public static OtpErlangObject unmonitoredCall(OtpMbox callerMbox, OtpNode callerNode, String serverName, String serverNode, OtpErlangObject request, int timeout) throws OtpErlangExit, OtpErlangDecodeException {
        return Generic.unmonitoredCall(
                callerMbox,
                callerNode,
                serverName,
                serverNode,
                GEN_CALL,
                request,
                timeout
        );
    }
}
