package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiElement;

/**
 * Created by luke.imhoff on 1/1/15.
 */
public interface Quotable extends PsiElement {
    public OtpErlangObject quote();
}
