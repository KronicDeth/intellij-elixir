// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirMatchedUnqualifiedParenthesesCall extends ElixirMatchedExpression, MatchedCall, UnqualifiedParenthesesCall {

  @NotNull
  ElixirIdentifier getIdentifier();

  @NotNull
  ElixirMatchedParenthesesArguments getMatchedParenthesesArguments();

  @Nullable
  String functionName();

  @NotNull
  PsiElement functionNameElement();

  @Nullable
  ElixirDoBlock getDoBlock();

  PsiElement getNameIdentifier();

  @NotNull
  ItemPresentation getPresentation();

  boolean isCalling(String resolvedModuleName, String resolvedFunctionName);

  boolean isCalling(String resolvedModuleName, String resolvedFunctionName, int resolvedFinalArity);

  boolean isCallingMacro(String resolvedModuleName, String resolvedFunctionName, int resolvedFinalArity);

  @Nullable
  String moduleName();

  @NotNull
  PsiElement[] primaryArguments();

  @Nullable
  Integer primaryArity();

  @NotNull
  OtpErlangObject quote();

  @Nullable
  Integer resolvedFinalArity();

  @NotNull
  String resolvedFunctionName();

  @NotNull
  String resolvedModuleName();

  @Nullable
  Integer resolvedPrimaryArity();

  @Nullable
  Integer resolvedSecondaryArity();

  @Nullable
  PsiElement[] secondaryArguments();

  @Nullable
  Integer secondaryArity();

}
