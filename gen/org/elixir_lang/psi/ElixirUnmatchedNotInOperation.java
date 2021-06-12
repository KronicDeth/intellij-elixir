// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.operation.NotIn;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import kotlin.ranges.IntRange;

public interface ElixirUnmatchedNotInOperation extends ElixirUnmatchedExpression, Call, NotIn {

  @NotNull
  ElixirInInfixOperator getInInfixOperator();

  @NotNull
  ElixirNotInfixOperator getNotInfixOperator();

  @NotNull
  List<ElixirUnmatchedExpression> getUnmatchedExpressionList();

  @Nullable String functionName();

  @Nullable PsiElement functionNameElement();

  @Nullable ElixirDoBlock getDoBlock();

  //WARNING: getName(...) is skipped
  //matching getName(ElixirUnmatchedNotInOperation, ...)
  //methods are not found in ElixirPsiImplUtil

  //WARNING: getNameIdentifier(...) is skipped
  //matching getNameIdentifier(ElixirUnmatchedNotInOperation, ...)
  //methods are not found in ElixirPsiImplUtil

  boolean hasDoBlockOrKeyword();

  boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName);

  boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity);

  boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName);

  boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity);

  @Nullable Quotable leftOperand();

  @Nullable String moduleName();

  //WARNING: operator(...) is skipped
  //matching operator(ElixirUnmatchedNotInOperation, ...)
  //methods are not found in ElixirPsiImplUtil

  @NotNull PsiElement[] primaryArguments();

  @Nullable Integer primaryArity();

  boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place);

  @NotNull OtpErlangObject quote();

  int resolvedFinalArity();

  @NotNull IntRange resolvedFinalArityRange();

  @NotNull String resolvedModuleName();

  @Nullable Integer resolvedPrimaryArity();

  @Nullable Integer resolvedSecondaryArity();

  @Nullable Quotable rightOperand();

  @Nullable PsiElement[] secondaryArguments();

  @Nullable Integer secondaryArity();

  @NotNull PsiElement setName(@NotNull String newName);

}
