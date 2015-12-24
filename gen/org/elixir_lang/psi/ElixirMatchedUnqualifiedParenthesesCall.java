// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirMatchedUnqualifiedParenthesesCall extends ElixirMatchedExpression, MatchedCall, UnqualifiedParenthesesCall {

  @NotNull
  ElixirMatchedParenthesesArguments getMatchedParenthesesArguments();

  @Nullable
  String functionName();

  @NotNull
  ASTNode functionNameNode();

  @Nullable
  String moduleName();

  @Nullable
  ElixirDoBlock getDoBlock();

  @NotNull
  PsiElement[] primaryArguments();

  @NotNull
  OtpErlangObject quote();

  @NotNull
  String resolvedFunctionName();

  @NotNull
  String resolvedModuleName();

  @Nullable
  PsiElement[] secondaryArguments();

}
