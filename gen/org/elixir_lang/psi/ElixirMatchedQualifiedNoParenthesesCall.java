// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirMatchedQualifiedNoParenthesesCall extends ElixirMatchedExpression, MatchedCall, QualifiedNoParenthesesCall {

  @NotNull
  ElixirDotInfixOperator getDotInfixOperator();

  @NotNull
  ElixirMatchedExpression getMatchedExpression();

  @NotNull
  ElixirNoParenthesesOneArgument getNoParenthesesOneArgument();

  @NotNull
  ElixirRelativeIdentifier getRelativeIdentifier();

  @NotNull
  PsiElement[] primaryArguments();

  @Nullable
  String functionName();

  ASTNode functionNameNode();

  @Nullable
  ElixirDoBlock getDoBlock();

  @NotNull
  String moduleName();

  @NotNull
  OtpErlangObject quote();

  @NotNull
  String resolvedFunctionName();

  @NotNull
  String resolvedModuleName();

  @Nullable
  PsiElement[] secondaryArguments();

}
