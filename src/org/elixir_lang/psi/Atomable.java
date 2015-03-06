package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;

/**
 * Created by luke.imhoff on 3/5/15.
 */
public interface Atomable extends Quotable {
    public OtpErlangObject quoteAsAtom();
}
