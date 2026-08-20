// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.util.concurrency.annotations.RequiresReadLock;

public interface ElixirUnmatchedUnaryOperation extends ElixirUnmatchedExpression, UnaryOperation {

  @NotNull
  ElixirUnaryPrefixOperator getUnaryPrefixOperator();

  @Nullable
  ElixirUnmatchedExpression getUnmatchedExpression();

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

  @Nullable String moduleName();

  @Nullable Quotable operand();

  @NotNull Operator operator();

  @RequiresReadLock
  @NotNull PsiElement[] primaryArguments();

  @RequiresReadLock
  @Nullable Integer primaryArity();

  @NotNull OtpErlangObject quote();

  @Nullable PsiElement[] secondaryArguments();

  @RequiresReadLock
  @Nullable Integer secondaryArity();

  @RequiresReadLock
  int resolvedFinalArity();

  @RequiresReadLock
  @NotNull ArityInterval resolvedFinalArityInterval();

  @NotNull String resolvedModuleName();

  @RequiresReadLock
  @Nullable Integer resolvedPrimaryArity();

  @RequiresReadLock
  @Nullable Integer resolvedSecondaryArity();

  @NotNull PsiElement setName(@NotNull String newName);

}
