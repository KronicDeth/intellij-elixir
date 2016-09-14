// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ElixirMultipleAliases extends Quotable {

  @NotNull
  List<ElixirEmptyParentheses> getEmptyParenthesesList();

  @Nullable
  ElixirKeywords getKeywords();

  @NotNull
  List<ElixirUnmatchedExpression> getUnmatchedExpressionList();

  boolean processDeclarations(PsiScopeProcessor processor, ResolveState state, PsiElement lastParent, PsiElement entrance);

  @NotNull
  OtpErlangObject quote();

}
