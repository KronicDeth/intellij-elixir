// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.stub.MatchedQualifiedNoArgumentsCall;
import com.intellij.psi.StubBasedPsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import java.util.Set;
import kotlin.ranges.IntRange;

public interface ElixirMatchedQualifiedNoArgumentsCall extends ElixirMatchedExpression, MatchedCall, QualifiedNoArgumentsCall<MatchedQualifiedNoArgumentsCall>, StubBasedPsiElement<MatchedQualifiedNoArgumentsCall> {

  @NotNull
  ElixirDotInfixOperator getDotInfixOperator();

  @NotNull
  ElixirMatchedExpression getMatchedExpression();

  @NotNull
  ElixirRelativeIdentifier getRelativeIdentifier();

  @Nullable String canonicalName();

  @NotNull Set<String> canonicalNameSet();

  @Nullable String functionName();

  @NotNull PsiElement functionNameElement();

  @Nullable ElixirDoBlock getDoBlock();

  boolean hasDoBlockOrKeyword();

  @Nullable String getName();

  @Nullable PsiElement getNameIdentifier();

  @NotNull ItemPresentation getPresentation();

  @Nullable PsiReference getReference();

  //WARNING: getStub(...) is skipped
  //matching getStub(ElixirMatchedQualifiedNoArgumentsCall, ...)
  //methods are not found in ElixirPsiImplUtil

  //WARNING: getUseScope(...) is skipped
  //matching getUseScope(ElixirMatchedQualifiedNoArgumentsCall, ...)
  //methods are not found in ElixirPsiImplUtil

  boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName);

  boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity);

  boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName);

  boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity);

  @NotNull String moduleName();

  @Nullable PsiElement[] primaryArguments();

  @Nullable Integer primaryArity();

  boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place);

  @NotNull PsiElement qualifier();

  @NotNull OtpErlangObject quote();

  int resolvedFinalArity();

  @NotNull IntRange resolvedFinalArityRange();

  @NotNull String resolvedModuleName();

  @Nullable Integer resolvedPrimaryArity();

  @Nullable Integer resolvedSecondaryArity();

  @Nullable PsiElement[] secondaryArguments();

  @Nullable Integer secondaryArity();

  @NotNull PsiElement setName(@NotNull String newName);

}
