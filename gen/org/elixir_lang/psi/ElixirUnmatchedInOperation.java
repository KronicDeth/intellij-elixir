// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import kotlin.ranges.IntRange;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.operation.In;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ElixirUnmatchedInOperation extends ElixirUnmatchedExpression, Call, In {

  @NotNull
  ElixirInInfixOperator getInInfixOperator();

  @NotNull
  List<ElixirUnmatchedExpression> getUnmatchedExpressionList();

  @Nullable
  String functionName();

  @NotNull
  PsiElement functionNameElement();

  @Nullable
  ElixirDoBlock getDoBlock();

  //WARNING: getName(...) is skipped
  //matching getName(ElixirUnmatchedInOperation, ...)
  //methods are not found in ElixirPsiImplUtil

  //WARNING: getNameIdentifier(...) is skipped
  //matching getNameIdentifier(ElixirUnmatchedInOperation, ...)
  //methods are not found in ElixirPsiImplUtil

  boolean hasDoBlockOrKeyword();

  boolean isCalling(String resolvedModuleName, String functionName);

  boolean isCalling(String resolvedModuleName, String functionName, int resolvedFinalArity);

  boolean isCallingMacro(String resolvedModuleName, String functionName);

  boolean isCallingMacro(String resolvedModuleName, String functionName, int resolvedFinalArity);

  @Nullable
  Quotable leftOperand();

  @Nullable
  String moduleName();

  @NotNull
  Operator operator();

  @NotNull
  PsiElement[] primaryArguments();

  @Nullable
  Integer primaryArity();

  boolean processDeclarations(PsiScopeProcessor processor, ResolveState state, PsiElement lastParent, PsiElement place);

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
  Quotable rightOperand();

  @Nullable
  PsiElement[] secondaryArguments();

  @Nullable
  Integer secondaryArity();

  @NotNull
  PsiElement setName(String newName);

}
