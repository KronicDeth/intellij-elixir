// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiLanguageInjectionHost;

public interface ElixirLiteralSigilHeredoc extends Literal, SigilHeredoc {

  @Nullable
  ElixirHeredocPrefix getHeredocPrefix();

  @NotNull
  List<ElixirLiteralHeredocLine> getLiteralHeredocLineList();

  @Nullable
  ElixirSigilModifiers getSigilModifiers();

  @NotNull List<Integer> addEscapedCharacterCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode child);

  @NotNull List<Integer> addEscapedEOL(@Nullable List<Integer> maybeCodePointList, @NotNull ASTNode child);

  @NotNull List<Integer> addEscapedTerminator(@Nullable List<Integer> maybeCodePointList, @NotNull ASTNode child);

  @NotNull List<Integer> addFragmentCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode child);

  @NotNull List<Integer> addHexadecimalEscapeSequenceCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode child);

  @NotNull LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper();

  @NotNull List<? extends HeredocLine> getHeredocLineList();

  @NotNull Integer indentation();

  boolean isValidHost();

  @NotNull OtpErlangObject quote();

  @NotNull OtpErlangObject quote(@NotNull OtpErlangObject quotedContent);

  @NotNull OtpErlangObject quoteBinary(OtpErlangList metadata, List<OtpErlangObject> argumentList);

  @NotNull OtpErlangObject quoteEmpty();

  @NotNull OtpErlangObject quoteInterpolation(ElixirInterpolation interpolation);

  @NotNull OtpErlangObject quoteLiteral(List<Integer> codePointList);

  @NotNull String sigilDelimiter();

  char sigilName();

  PsiLanguageInjectionHost updateText(@NotNull String text);

}
