// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.operation.In;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ElixirUnmatchedInOperation extends ElixirUnmatchedExpression, In {

  @NotNull
  ElixirInInfixOperator getInInfixOperator();

  @NotNull
  List<ElixirUnmatchedExpression> getUnmatchedExpressionList();

  @NotNull
  PsiElement functionNameElement();

  @Nullable
  ElixirDoBlock getDoBlock();

  @NotNull
  Quotable leftOperand();

  @Nullable
  String moduleName();

  @NotNull
  Operator operator();

  @NotNull
  PsiElement[] primaryArguments();

  @NotNull
  OtpErlangObject quote();

  @NotNull
  String resolvedModuleName();

  @NotNull
  Quotable rightOperand();

  @Nullable
  PsiElement[] secondaryArguments();

}
