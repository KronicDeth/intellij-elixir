// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.stub.MatchedQualifiedParenthesesCall;
import com.intellij.psi.StubBasedPsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.concurrency.annotations.RequiresReadLock;
import java.util.Set;

public interface ElixirMatchedQualifiedParenthesesCall extends ElixirMatchedExpression, MatchedCall, QualifiedParenthesesCall<MatchedQualifiedParenthesesCall>, StubBasedPsiElement<MatchedQualifiedParenthesesCall> {

  @NotNull
  ElixirDotInfixOperator getDotInfixOperator();

  @NotNull
  ElixirMatchedExpression getMatchedExpression();

  @NotNull
  ElixirMatchedParenthesesArguments getMatchedParenthesesArguments();

  @NotNull
  ElixirRelativeIdentifier getRelativeIdentifier();

  @RequiresReadLock
  @Nullable String canonicalName();

  @RequiresReadLock
  @NotNull Set<String> canonicalNameSet();

  @RequiresReadLock
  @Nullable String functionName();

  @RequiresReadLock
  @NotNull PsiElement functionNameElement();

  @Nullable ElixirDoBlock getDoBlock();

  @RequiresReadLock
  boolean hasDoBlockOrKeyword();

  @RequiresReadLock
  @Nullable String getName();

  @RequiresReadLock
  @Nullable PsiElement getNameIdentifier();

  @RequiresReadLock
  @NotNull ItemPresentation getPresentation();

  @Nullable PsiReference getReference();

  //WARNING: getStub(...) is skipped
  //matching getStub(ElixirMatchedQualifiedParenthesesCall, ...)
  //methods are not found in ElixirPsiImplUtil

  //WARNING: getUseScope(...) is skipped
  //matching getUseScope(ElixirMatchedQualifiedParenthesesCall, ...)
  //methods are not found in ElixirPsiImplUtil

  @RequiresReadLock
  @Nullable String implementedProtocolName();

  @RequiresReadLock
  boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName);

  @RequiresReadLock
  boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity);

  @RequiresReadLock
  boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName);

  @RequiresReadLock
  boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity);

  @RequiresReadLock
  @NotNull String moduleName();

  @RequiresReadLock
  @NotNull PsiElement[] primaryArguments();

  @RequiresReadLock
  @Nullable Integer primaryArity();

  @RequiresReadLock
  boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place);

  @NotNull PsiElement qualifier();

  @NotNull OtpErlangObject quote();

  @RequiresReadLock
  int resolvedFinalArity();

  @RequiresReadLock
  @NotNull ArityInterval resolvedFinalArityInterval();

  @RequiresReadLock
  @NotNull String resolvedModuleName();

  @RequiresReadLock
  @Nullable Integer resolvedPrimaryArity();

  @RequiresReadLock
  @Nullable Integer resolvedSecondaryArity();

  @RequiresReadLock
  @Nullable PsiElement[] secondaryArguments();

  @RequiresReadLock
  @Nullable Integer secondaryArity();

  @NotNull PsiElement setName(@NotNull String newName);

}
