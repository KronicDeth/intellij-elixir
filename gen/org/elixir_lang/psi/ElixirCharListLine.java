// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;

public interface ElixirCharListLine extends Atomable, InterpolatedCharList, Line, Quotable {

  @Nullable
  ElixirCharListLineBody getCharListLineBody();

  @NotNull List<Integer> addEscapedCharacterCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode child);

  @NotNull List<Integer> addEscapedEOL(@Nullable List<Integer> maybeCodePointList, @NotNull ASTNode child);

  @NotNull List<Integer> addFragmentCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode child);

  @NotNull List<Integer> addHexadecimalEscapeSequenceCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode child);

  @Nullable Body getBody();

  IElementType getFragmentType();

  @NotNull OtpErlangObject quote();

  @NotNull OtpErlangObject quoteAsAtom();

  @NotNull OtpErlangObject quoteBinary(OtpErlangList metadata, List<OtpErlangObject> argumentList);

  @NotNull OtpErlangObject quoteEmpty();

  @NotNull OtpErlangObject quoteInterpolation(ElixirInterpolation interpolation);

  @NotNull OtpErlangObject quoteLiteral(List<Integer> codePointList);

}
