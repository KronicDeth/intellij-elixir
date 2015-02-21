package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by luke.imhoff on 2/4/15.
 */
public interface Parent extends Fragmented, PsiElement {
    @NotNull
    public List<Integer> addEscapedCharacterCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode child);

    @NotNull
    public List<Integer> addFragmentCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode child);

    @NotNull
    public List<Integer> addHexadecimalEscapeSequenceCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode child);

    public OtpErlangObject quoteBinary(OtpErlangTuple binaryConstruction);

    public OtpErlangObject quoteEmpty();

    public OtpErlangObject quoteLiteral(List<Integer> codePointList);
}
