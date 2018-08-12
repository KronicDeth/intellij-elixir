// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import kotlin.ranges.IntRange;
import org.elixir_lang.psi.call.StubBased;
import org.elixir_lang.psi.call.arguments.star.NoParentheses;
import org.elixir_lang.psi.qualification.Unqualified;
import org.elixir_lang.psi.stub.UnqualifiedNoParenthesesManyArgumentsCall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public interface ElixirUnqualifiedNoParenthesesManyArgumentsCall extends PsiElement, StubBased<UnqualifiedNoParenthesesManyArgumentsCall>, NoParentheses, Unqualified, Quotable, StubBasedPsiElement<UnqualifiedNoParenthesesManyArgumentsCall> {

  @NotNull
  List<ElixirEmptyParentheses> getEmptyParenthesesList();

  @NotNull
  ElixirIdentifier getIdentifier();

  @NotNull
  List<ElixirMatchedExpression> getMatchedExpressionList();

  @Nullable
  ElixirNoParenthesesKeywords getNoParenthesesKeywords();

  @NotNull
  List<ElixirNoParenthesesManyStrictNoParenthesesExpression> getNoParenthesesManyStrictNoParenthesesExpressionList();

  @Nullable
  ElixirNoParenthesesStrict getNoParenthesesStrict();

  @Nullable
  String canonicalName();

  @NotNull
  Set<String> canonicalNameSet();

  @Nullable
  String functionName();

  @NotNull
  PsiElement functionNameElement();

  @Nullable
  ElixirDoBlock getDoBlock();

  boolean hasDoBlockOrKeyword();

  @Nullable
  String getName();

  @Nullable
  PsiElement getNameIdentifier();

  @NotNull
  ItemPresentation getPresentation();

  @Nullable
  PsiReference getReference();

  //WARNING: getStub(...) is skipped
  //matching getStub(ElixirUnqualifiedNoParenthesesManyArgumentsCall, ...)
  //methods are not found in ElixirPsiImplUtil

  //WARNING: getUseScope(...) is skipped
  //matching getUseScope(ElixirUnqualifiedNoParenthesesManyArgumentsCall, ...)
  //methods are not found in ElixirPsiImplUtil

  boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName);

  boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity);

  boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName);

  boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity);

  @Nullable
  String moduleName();

  @NotNull
  PsiElement[] primaryArguments();

  @Nullable
  Integer primaryArity();

  boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place);

  @NotNull
  OtpErlangObject quote();

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
  PsiElement setName(@NotNull String newName);

}
