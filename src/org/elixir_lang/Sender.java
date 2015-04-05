package org.elixir_lang;

import com.ericsson.otp.erlang.OtpErlangObject;

/**
 * Interface for sending messages from OtpMbox that handles differences between pids and registered remote names
 */
public interface Sender {
    void send(OtpErlangObject message);
}
