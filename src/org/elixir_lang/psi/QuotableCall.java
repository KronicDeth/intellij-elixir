package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;

/*
 * A function call that can be quoted but doesn't directly expose it's arguments and identifier because they are
 * derived.
 */
public interface QuotableCall extends Quotable, QuotableArguments {
    public OtpErlangObject quoteIdentifier();
}
