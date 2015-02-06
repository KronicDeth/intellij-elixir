package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.intellij.psi.PsiElement;

import java.util.List;

/**
 * Created by luke.imhoff on 2/4/15.
 */
public interface Interpolated extends Fragmented, PsiElement {
    public OtpErlangObject quoteBinary(OtpErlangTuple binaryConstruction);

    public OtpErlangObject quoteEmpty();

    public OtpErlangObject quoteLiteral(List<Integer> codePointList);
}
