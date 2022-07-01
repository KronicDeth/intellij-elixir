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

public interface ElixirHeredoc extends Heredoc, Quote {

  @NotNull
  List<ElixirHeredocLine> getHeredocLineList();

  @Nullable
  ElixirHeredocPrefix getHeredocPrefix();

  @NotNull List<Integer> addEscapedCharacterCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode child);

  @NotNull List<Integer> addEscapedEOL(@Nullable List<Integer> maybeCodePointList, @NotNull ASTNode child);

  @NotNull List<Integer> addEscapedTerminator(@Nullable List<Integer> maybeCodePointList, @NotNull ASTNode child);

  @NotNull List<Integer> addFragmentCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode child);

  @NotNull List<Integer> addHexadecimalEscapeSequenceCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode child);

  @NotNull LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper();

  //WARNING: getHeredocLineList(...) is skipped
  //matching getHeredocLineList(ElixirHeredoc, ...)
  //methods are not found in ElixirPsiImplUtil

  boolean isCharList();

  boolean isValidHost();

  @NotNull OtpErlangObject quote();

  @NotNull OtpErlangObject quoteBinary(OtpErlangList metadata, List<OtpErlangObject> argumentList);

  @NotNull OtpErlangObject quoteEmpty();

  @NotNull OtpErlangObject quoteInterpolation(ElixirInterpolation interpolation);

  @NotNull OtpErlangObject quoteLiteral(List<Integer> codePointList);

  PsiLanguageInjectionHost updateText(@NotNull String text);

}
