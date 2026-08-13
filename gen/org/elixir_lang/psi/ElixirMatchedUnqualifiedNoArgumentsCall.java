// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.stub.MatchedUnqualifiedNoArgumentsCall;
import com.intellij.psi.StubBasedPsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.concurrency.annotations.RequiresReadLock;
import java.util.Set;

public interface ElixirMatchedUnqualifiedNoArgumentsCall extends ElixirMatchedExpression, MatchedCall, UnqualifiedNoArgumentsCall<MatchedUnqualifiedNoArgumentsCall>, StubBasedPsiElement<MatchedUnqualifiedNoArgumentsCall> {

  @NotNull
  ElixirIdentifier getIdentifier();

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
  //matching getStub(ElixirMatchedUnqualifiedNoArgumentsCall, ...)
  //methods are not found in ElixirPsiImplUtil

  @NotNull SearchScope getUseScope();

  @RequiresReadLock
  boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName);

  @RequiresReadLock
  boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity);

  @RequiresReadLock
  boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName);

  @RequiresReadLock
  boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity);

  @RequiresReadLock
  @Nullable String implementedProtocolName();

  @Nullable String moduleName();

  @Nullable PsiElement[] primaryArguments();

  @RequiresReadLock
  @Nullable Integer primaryArity();

  @RequiresReadLock
  boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place);

  @RequiresReadLock
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

}
