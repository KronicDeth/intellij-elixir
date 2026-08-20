// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.stub.MatchedUnqualifiedNoParenthesesCall;
import com.intellij.psi.StubBasedPsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.concurrency.annotations.RequiresReadLock;
import java.util.Set;

public interface ElixirMatchedUnqualifiedNoParenthesesCall extends ElixirMatchedExpression, MatchedCall, UnqualifiedNoParenthesesCall<MatchedUnqualifiedNoParenthesesCall>, StubBasedPsiElement<MatchedUnqualifiedNoParenthesesCall> {

  @NotNull
  ElixirIdentifier getIdentifier();

  @NotNull
  ElixirNoParenthesesOneArgument getNoParenthesesOneArgument();

  @RequiresReadLock
  @Nullable String canonicalName();

  @RequiresReadLock
  @NotNull Set<String> canonicalNameSet();

  @RequiresReadLock
  int exportedArity(@NotNull ResolveState state);

  @RequiresReadLock
  @Nullable String exportedName();

  @RequiresReadLock
  @Nullable String functionName();

  @RequiresReadLock
  @NotNull PsiElement functionNameElement();

  @Nullable ElixirDoBlock getDoBlock();

  @RequiresReadLock
  boolean hasDoBlockOrKeyword();

  @RequiresReadLock
  boolean isExported();

  @RequiresReadLock
  @Nullable String getName();

  @RequiresReadLock
  @Nullable PsiElement getNameIdentifier();

  @RequiresReadLock
  @NotNull ItemPresentation getPresentation();

  @Nullable PsiReference getReference();

  //WARNING: getStub(...) is skipped
  //matching getStub(ElixirMatchedUnqualifiedNoParenthesesCall, ...)
  //methods are not found in ElixirPsiImplUtil

  //WARNING: getUseScope(...) is skipped
  //matching getUseScope(ElixirMatchedUnqualifiedNoParenthesesCall, ...)
  //methods are not found in ElixirPsiImplUtil

  @RequiresReadLock
  boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName);

  @RequiresReadLock
  boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity);

  @RequiresReadLock
  boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName);

  @RequiresReadLock
  boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity);

  @Nullable String moduleName();

  @RequiresReadLock
  @NotNull PsiElement[] primaryArguments();

  @RequiresReadLock
  @Nullable Integer primaryArity();

  @RequiresReadLock
  boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place);

  @RequiresReadLock
  @Nullable String implementedProtocolName();

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

  @Nullable PsiElement[] secondaryArguments();

  @RequiresReadLock
  @Nullable Integer secondaryArity();

  @NotNull PsiElement setName(@NotNull String newName);

  //WARNING: getStub(...) is skipped
  //matching getStub(ElixirMatchedUnqualifiedNoParenthesesCall, ...)
  //methods are not found in ElixirPsiImplUtil

  //WARNING: getUseScope(...) is skipped
  //matching getUseScope(ElixirMatchedUnqualifiedNoParenthesesCall, ...)
  //methods are not found in ElixirPsiImplUtil

}
