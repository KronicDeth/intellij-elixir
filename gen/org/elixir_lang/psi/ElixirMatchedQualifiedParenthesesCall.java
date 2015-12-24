// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirMatchedQualifiedParenthesesCall extends ElixirMatchedExpression, MatchedCall, QualifiedParenthesesCall {

  @NotNull
  ElixirDotInfixOperator getDotInfixOperator();

  @NotNull
  ElixirMatchedExpression getMatchedExpression();

  @NotNull
  ElixirMatchedParenthesesArguments getMatchedParenthesesArguments();

  @NotNull
  ElixirRelativeIdentifier getRelativeIdentifier();

  @Nullable
  String functionName();

  ASTNode functionNameNode();

  @Nullable
  ElixirDoBlock getDoBlock();

  @NotNull
  String moduleName();

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
