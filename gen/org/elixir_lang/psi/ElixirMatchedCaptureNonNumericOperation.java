// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiElement;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.psi.call.Named;
import org.elixir_lang.psi.operation.Prefix;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirMatchedCaptureNonNumericOperation extends ElixirMatchedExpression, Named, Prefix {

  @NotNull
  ElixirCapturePrefixOperator getCapturePrefixOperator();

  @Nullable
  ElixirMatchedExpression getMatchedExpression();

  @Nullable
  String functionName();

  @NotNull
  PsiElement functionNameElement();

  @Nullable
  ElixirDoBlock getDoBlock();

  @Nullable
  String getName();

  PsiElement getNameIdentifier();

  boolean hasDoBlockOrKeyword();

  boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName);

  boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity);

  boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName);

  boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity);

  @Nullable
  String moduleName();

  @Nullable
  Quotable operand();

  @NotNull
  Operator operator();

  @NotNull
  PsiElement[] primaryArguments();

  @Nullable
  Integer primaryArity();

  @NotNull
  OtpErlangObject quote();

  @Nullable
  PsiElement[] secondaryArguments();

  @Nullable
  Integer secondaryArity();

  @NotNull
  int resolvedFinalArity();

  @NotNull
  IntRange resolvedFinalArityRange();

  @NotNull
  String resolvedModuleName();

  @Nullable
  Integer resolvedPrimaryArity();

  @Nullable
  Integer resolvedSecondaryArity();

  @NotNull
  PsiElement setName(String newName);

}
