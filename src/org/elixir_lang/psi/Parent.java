package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangObject;
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
    List<Integer> addEscapedCharacterCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode child);

    @NotNull
    List<Integer> addEscapedEOL(@Nullable List<Integer> codePointList, @NotNull ASTNode child);

    @NotNull
    List<Integer> addFragmentCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode child);

    @NotNull
    List<Integer> addHexadecimalEscapeSequenceCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode child);

    OtpErlangObject quoteBinary(OtpErlangList metadata, List<OtpErlangObject> argumentList);

    OtpErlangObject quoteEmpty();

    OtpErlangObject quoteInterpolation(ElixirInterpolation interpolation);

    OtpErlangObject quoteLiteral(List<Integer> codePointList);
}
