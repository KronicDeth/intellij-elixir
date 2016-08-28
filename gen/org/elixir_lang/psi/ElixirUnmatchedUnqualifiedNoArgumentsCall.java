// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.SearchScope;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.psi.stub.UnmatchedUnqualifiedNoArgumentsCall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ElixirUnmatchedUnqualifiedNoArgumentsCall extends ElixirUnmatchedExpression, UnqualifiedNoArgumentsCall<UnmatchedUnqualifiedNoArgumentsCall>, StubBasedPsiElement<UnmatchedUnqualifiedNoArgumentsCall> {

  @Nullable
  ElixirDoBlock getDoBlock();

  @NotNull
  ElixirIdentifier getIdentifier();

  @Nullable
  String canonicalName();

  @Nullable
  Collection<String> canonicalNameCollection();

  @Nullable
  String functionName();

  @NotNull
  PsiElement functionNameElement();

  boolean hasDoBlockOrKeyword();

  @Nullable
  String getName();

  PsiElement getNameIdentifier();

  @Nullable
  PsiReference getReference();

  @NotNull
  SearchScope getUseScope();

  boolean isCalling(String resolvedModuleName, String resolvedFunctionName);

  boolean isCalling(String resolvedModuleName, String resolvedFunctionName, int resolvedFinalArity);

  boolean isCallingMacro(String resolvedModuleName, String resolvedFunctionName);

  boolean isCallingMacro(String resolvedModuleName, String resolvedFunctionName, int resolvedFinalArity);

  @Nullable
  String moduleName();

  @Nullable
  PsiElement[] primaryArguments();

  @Nullable
  Integer primaryArity();

  boolean processDeclarations(PsiScopeProcessor processor, ResolveState state, PsiElement lastParent, PsiElement place);

  @NotNull
  OtpErlangObject quote();

  @NotNull
  int resolvedFinalArity();

  @NotNull
  IntRange resolvedFinalArityRange();

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

  @NotNull
  PsiElement setName(String newName);

}
