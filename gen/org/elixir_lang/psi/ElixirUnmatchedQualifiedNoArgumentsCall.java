// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.psi.stub.UnmatchedQualifiedNoArgumentsCall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface ElixirUnmatchedQualifiedNoArgumentsCall extends ElixirUnmatchedExpression, QualifiedNoArgumentsCall<UnmatchedQualifiedNoArgumentsCall>, StubBasedPsiElement<UnmatchedQualifiedNoArgumentsCall> {

  @Nullable
  ElixirDoBlock getDoBlock();

  @NotNull
  ElixirDotInfixOperator getDotInfixOperator();

  @NotNull
  ElixirRelativeIdentifier getRelativeIdentifier();

  @NotNull
  ElixirUnmatchedExpression getUnmatchedExpression();

  @Nullable
  String canonicalName();

  @NotNull
  Set<String> canonicalNameSet();

  @Nullable
  String functionName();

  PsiElement functionNameElement();

  boolean hasDoBlockOrKeyword();

  @Nullable
  String getName();

  PsiElement getNameIdentifier();

  @Nullable
  PsiReference getReference();

  boolean isCalling(String resolvedModuleName, String functionName);

  boolean isCalling(String resolvedModuleName, String functionName, int resolvedFinalArity);

  boolean isCallingMacro(String resolvedModuleName, String functionName);

  boolean isCallingMacro(String resolvedModuleName, String functionName, int resolvedFinalArity);

  @NotNull
  String moduleName();

  @Nullable
  PsiElement[] primaryArguments();

  @Nullable
  Integer primaryArity();

  boolean processDeclarations(PsiScopeProcessor processor, ResolveState state, PsiElement lastParent, PsiElement place);

  @NotNull
  PsiElement qualifier();

  @NotNull
  OtpErlangObject quote();

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

  @Nullable
  PsiElement[] secondaryArguments();

  @Nullable
  Integer secondaryArity();

  @NotNull
  PsiElement setName(String newName);

}
