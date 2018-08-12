// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ElixirStabBody extends Quotable {

  @NotNull
  List<ElixirEex> getEexList();

  @NotNull
  List<ElixirEmptyParentheses> getEmptyParenthesesList();

  @NotNull
  List<ElixirEndOfExpression> getEndOfExpressionList();

  @NotNull
  List<ElixirUnmatchedExpression> getUnmatchedExpressionList();

  @NotNull
  List<ElixirUnqualifiedNoParenthesesManyArgumentsCall> getUnqualifiedNoParenthesesManyArgumentsCallList();

  boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place);

  @NotNull
  OtpErlangObject quote();

}
