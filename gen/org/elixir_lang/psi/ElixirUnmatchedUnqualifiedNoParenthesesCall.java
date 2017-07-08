// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.psi.stub.UnmatchedUnqualifiedNoParenthesesCall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface ElixirUnmatchedUnqualifiedNoParenthesesCall extends ElixirUnmatchedExpression, UnqualifiedNoParenthesesCall<UnmatchedUnqualifiedNoParenthesesCall>, StubBasedPsiElement<UnmatchedUnqualifiedNoParenthesesCall> {

  @Nullable
  ElixirDoBlock getDoBlock();

  @NotNull
  ElixirIdentifier getIdentifier();

  @NotNull
  ElixirNoParenthesesOneArgument getNoParenthesesOneArgument();

  @Nullable
  String canonicalName();

  @NotNull
  Set<String> canonicalNameSet();

  int exportedArity();

  @Nullable
  String exportedName();

  @Nullable
  String functionName();

  @NotNull
  PsiElement functionNameElement();

  //WARNING: getDoBlock(...) is skipped
  //matching getDoBlock(ElixirUnmatchedUnqualifiedNoParenthesesCall, ...)
  //methods are not found in ElixirPsiImplUtil

  boolean hasDoBlockOrKeyword();

  boolean isExported();

  @Nullable
  String getName();

  PsiElement getNameIdentifier();

  @NotNull
  ItemPresentation getPresentation();

  @Nullable
  PsiReference getReference();

  //WARNING: getStub(...) is skipped
  //matching getStub(ElixirUnmatchedUnqualifiedNoParenthesesCall, ...)
  //methods are not found in ElixirPsiImplUtil

  //WARNING: getUseScope(...) is skipped
  //matching getUseScope(ElixirUnmatchedUnqualifiedNoParenthesesCall, ...)
  //methods are not found in ElixirPsiImplUtil

  boolean isCalling(String resolvedModuleName, String functionName);

  boolean isCalling(String resolvedModuleName, String functionName, int resolvedFinalArity);

  boolean isCallingMacro(String resolvedModuleName, String functionName);

  boolean isCallingMacro(String resolvedModuleName, String functionName, int resolvedFinalArity);

  @Nullable
  String moduleName();

  @NotNull
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
