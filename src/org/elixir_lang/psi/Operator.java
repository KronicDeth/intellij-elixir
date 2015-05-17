package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangList;
import com.intellij.psi.tree.TokenSet;

/**
 * An operator that is composed of an operator token and EOLs before or after the token.
 *
 * Created by luke.imhoff on 3/18/15.
 */
public interface Operator extends Quotable {
    public TokenSet operatorTokenSet();
}
