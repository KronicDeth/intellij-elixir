// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ElixirMatchedDotCall extends ElixirMatchedExpression, DotCall, MatchedCall {

  @NotNull
  ElixirDotInfixOperator getDotInfixOperator();

  @NotNull
  ElixirMatchedExpression getMatchedExpression();

  @NotNull
  List<ElixirParenthesesArguments> getParenthesesArgumentsList();

  @Nullable
  String functionName();

  @Nullable
  ASTNode functionNameNode();

  @Nullable
  ElixirDoBlock getDoBlock();

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
