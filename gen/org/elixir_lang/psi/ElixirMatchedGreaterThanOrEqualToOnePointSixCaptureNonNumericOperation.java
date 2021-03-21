// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.call.Named;
import org.elixir_lang.psi.operation.capture.NonNumeric;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiReference;
import kotlin.ranges.IntRange;

public interface ElixirMatchedGreaterThanOrEqualToOnePointSixCaptureNonNumericOperation extends ElixirMatchedExpression, Named, NonNumeric {

  @NotNull
  ElixirCapturePrefixOperator getCapturePrefixOperator();

  @Nullable
  ElixirMatchedExpression getMatchedExpression();

  @Nullable String functionName();

  @NotNull PsiElement functionNameElement();

  @Nullable ElixirDoBlock getDoBlock();

  @Nullable String getName();

  @Nullable PsiElement getNameIdentifier();

  @Nullable PsiReference getReference();

  boolean hasDoBlockOrKeyword();

  boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName);

  boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity);

  boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName);

  boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity);

  @Nullable String moduleName();

  @Nullable Quotable operand();

  @NotNull Operator operator();

  @NotNull PsiElement[] primaryArguments();

  @Nullable Integer primaryArity();

  @NotNull OtpErlangObject quote();

  @Nullable PsiElement[] secondaryArguments();

  @Nullable Integer secondaryArity();

  int resolvedFinalArity();

  @NotNull IntRange resolvedFinalArityRange();

  @NotNull String resolvedModuleName();

  @Nullable Integer resolvedPrimaryArity();

  @Nullable Integer resolvedSecondaryArity();

  @NotNull PsiElement setName(@NotNull String newName);

}
