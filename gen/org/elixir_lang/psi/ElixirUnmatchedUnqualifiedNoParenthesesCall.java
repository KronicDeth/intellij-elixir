// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.stub.UnmatchedUnqualifiedNoParenthesesCall;
import com.intellij.psi.StubBasedPsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import java.util.Set;
import kotlin.ranges.IntRange;

public interface ElixirUnmatchedUnqualifiedNoParenthesesCall extends ElixirUnmatchedExpression, UnqualifiedNoParenthesesCall<UnmatchedUnqualifiedNoParenthesesCall>, StubBasedPsiElement<UnmatchedUnqualifiedNoParenthesesCall> {

  @Nullable
  ElixirDoBlock getDoBlock();

  @NotNull
  ElixirIdentifier getIdentifier();

  @NotNull
  ElixirNoParenthesesOneArgument getNoParenthesesOneArgument();

  @Nullable String canonicalName();

  @NotNull Set<String> canonicalNameSet();

  int exportedArity();

  @Nullable String exportedName();

  @Nullable String functionName();

  @NotNull PsiElement functionNameElement();

  //WARNING: getDoBlock(...) is skipped
  //matching getDoBlock(ElixirUnmatchedUnqualifiedNoParenthesesCall, ...)
  //methods are not found in ElixirPsiImplUtil

  boolean hasDoBlockOrKeyword();

  boolean isExported();

  @Nullable String getName();

  @Nullable PsiElement getNameIdentifier();

  @NotNull ItemPresentation getPresentation();

  @Nullable PsiReference getReference();

  //WARNING: getStub(...) is skipped
  //matching getStub(ElixirUnmatchedUnqualifiedNoParenthesesCall, ...)
  //methods are not found in ElixirPsiImplUtil

  //WARNING: getUseScope(...) is skipped
  //matching getUseScope(ElixirUnmatchedUnqualifiedNoParenthesesCall, ...)
  //methods are not found in ElixirPsiImplUtil

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

  @NotNull String resolvedModuleName();

  @Nullable Integer resolvedPrimaryArity();

  @Nullable Integer resolvedSecondaryArity();

  @Nullable PsiElement[] secondaryArguments();

  @Nullable Integer secondaryArity();

  @NotNull PsiElement setName(@NotNull String newName);

  //WARNING: getDoBlock(...) is skipped
  //matching getDoBlock(ElixirUnmatchedUnqualifiedNoParenthesesCall, ...)
  //methods are not found in ElixirPsiImplUtil

  //WARNING: getStub(...) is skipped
  //matching getStub(ElixirUnmatchedUnqualifiedNoParenthesesCall, ...)
  //methods are not found in ElixirPsiImplUtil

  //WARNING: getUseScope(...) is skipped
  //matching getUseScope(ElixirUnmatchedUnqualifiedNoParenthesesCall, ...)
  //methods are not found in ElixirPsiImplUtil

}
