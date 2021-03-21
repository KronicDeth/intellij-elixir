// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.stub.UnmatchedAtUnqualifiedNoParenthesesCall;
import com.intellij.psi.StubBasedPsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.SearchScope;
import java.util.Set;
import kotlin.ranges.IntRange;

public interface ElixirUnmatchedAtUnqualifiedNoParenthesesCall extends ElixirUnmatchedExpression, AtUnqualifiedNoParenthesesCall<UnmatchedAtUnqualifiedNoParenthesesCall>, StubBasedPsiElement<UnmatchedAtUnqualifiedNoParenthesesCall> {

  @NotNull
  ElixirAtIdentifier getAtIdentifier();

  @Nullable
  ElixirDoBlock getDoBlock();

  @NotNull
  ElixirNoParenthesesOneArgument getNoParenthesesOneArgument();

  @Nullable String canonicalName();

  @NotNull Set<String> canonicalNameSet();

  @Nullable String functionName();

  @Nullable PsiElement functionNameElement();

  //WARNING: getDoBlock(...) is skipped
  //matching getDoBlock(ElixirUnmatchedAtUnqualifiedNoParenthesesCall, ...)
  //methods are not found in ElixirPsiImplUtil

  boolean hasDoBlockOrKeyword();

  @Nullable String getName();

  @Nullable PsiElement getNameIdentifier();

  @NotNull ItemPresentation getPresentation();

  @Nullable PsiReference getReference();

  //WARNING: getStub(...) is skipped
  //matching getStub(ElixirUnmatchedAtUnqualifiedNoParenthesesCall, ...)
  //methods are not found in ElixirPsiImplUtil

  @NotNull SearchScope getUseScope();

  boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName);

  boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity);

  boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName);

  boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity);

  @Nullable String moduleName();

  @NotNull PsiElement[] primaryArguments();

  @Nullable Integer primaryArity();

  boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place);

  @NotNull OtpErlangObject quote();

  int resolvedFinalArity();

  @NotNull IntRange resolvedFinalArityRange();

  @Nullable String resolvedModuleName();

  @Nullable Integer resolvedPrimaryArity();

  @Nullable Integer resolvedSecondaryArity();

  @Nullable PsiElement[] secondaryArguments();

  @Nullable Integer secondaryArity();

  @NotNull PsiElement setName(@NotNull String newName);

}
