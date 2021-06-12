// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;

public interface ElixirInterpolatedStringSigilHeredoc extends StringFragmented, InterpolatedSigilHeredoc, InterpolatedStringHeredocLined {

  @Nullable
  ElixirHeredocPrefix getHeredocPrefix();

  @NotNull
  List<ElixirInterpolatedStringHeredocLine> getInterpolatedStringHeredocLineList();

  @Nullable
  ElixirSigilModifiers getSigilModifiers();

  @NotNull List<Integer> addEscapedCharacterCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode child);

  @NotNull List<Integer> addEscapedEOL(@Nullable List<Integer> maybeCodePointList, @NotNull ASTNode child);

  @NotNull List<Integer> addFragmentCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode child);

  @NotNull List<Integer> addHexadecimalEscapeSequenceCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode child);

  IElementType getFragmentType();

  @NotNull List<HeredocLine> getHeredocLineList();

  @NotNull Integer indentation();

  @NotNull OtpErlangObject quote();

  @NotNull OtpErlangObject quote(@NotNull OtpErlangObject quotedContent);

  @NotNull OtpErlangObject quoteBinary(OtpErlangList metadata, List<OtpErlangObject> argumentList);

  @NotNull OtpErlangObject quoteEmpty();

  @NotNull OtpErlangObject quoteInterpolation(ElixirInterpolation interpolation);

  @NotNull OtpErlangObject quoteLiteral(List<Integer> codePointList);

  @NotNull String sigilDelimiter();

  char sigilName();

}
