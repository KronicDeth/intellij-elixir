// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ElixirUnmatchedDotCall extends ElixirUnmatchedExpression, DotCall {

  @Nullable
  ElixirDoBlock getDoBlock();

  @NotNull
  ElixirDotInfixOperator getDotInfixOperator();

  @NotNull
  List<ElixirParenthesesArguments> getParenthesesArgumentsList();

  @NotNull
  ElixirUnmatchedExpression getUnmatchedExpression();

  @Nullable
  String functionName();

  @Nullable
  ASTNode functionNameNode();

  @Nullable
  String moduleName();

  @NotNull
  PsiElement[] primaryArguments();

  @NotNull
  OtpErlangObject quote();

  @Nullable
  String resolvedFunctionName();

  @Nullable
  String resolvedModuleName();

  @Nullable
  PsiElement[] secondaryArguments();

}
