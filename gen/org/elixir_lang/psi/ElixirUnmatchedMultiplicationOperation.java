// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.call.Named;
import org.elixir_lang.psi.operation.Multiplication;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.concurrency.annotations.RequiresReadLock;

public interface ElixirUnmatchedMultiplicationOperation extends ElixirUnmatchedExpression, Named, Multiplication {

  @NotNull
  ElixirMultiplicationInfixOperator getMultiplicationInfixOperator();

  @NotNull
  List<ElixirUnmatchedExpression> getUnmatchedExpressionList();

  @RequiresReadLock
  @Nullable String functionName();

  @RequiresReadLock
  @NotNull PsiElement functionNameElement();

  @Nullable ElixirDoBlock getDoBlock();

  @RequiresReadLock
  @Nullable String getName();

  @RequiresReadLock
  @Nullable PsiElement getNameIdentifier();

  @RequiresReadLock
  boolean hasDoBlockOrKeyword();

  @RequiresReadLock
  boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName);

  @RequiresReadLock
  boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity);

  @RequiresReadLock
  boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName);

  @RequiresReadLock
  boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity);

  @Nullable Quotable leftOperand();

  @Nullable String moduleName();

  @NotNull Operator operator();

  @RequiresReadLock
  @NotNull PsiElement[] primaryArguments();

  @RequiresReadLock
  @Nullable Integer primaryArity();

  @RequiresReadLock
  boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place);

  @NotNull OtpErlangObject quote();

  @RequiresReadLock
  int resolvedFinalArity();

  @RequiresReadLock
  @NotNull ArityInterval resolvedFinalArityInterval();

  @NotNull String resolvedModuleName();

  @RequiresReadLock
  @Nullable Integer resolvedPrimaryArity();

  @RequiresReadLock
  @Nullable Integer resolvedSecondaryArity();

  @Nullable Quotable rightOperand();

  @Nullable PsiElement[] secondaryArguments();

  @RequiresReadLock
  @Nullable Integer secondaryArity();

  @NotNull PsiElement setName(@NotNull String newName);

}
