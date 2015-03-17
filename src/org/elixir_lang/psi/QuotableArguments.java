package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiElement;

/**
 * Created by luke.imhoff on 3/14/15.
 */
public interface QuotableArguments extends PsiElement {
    public OtpErlangObject[] quoteArguments();
}
