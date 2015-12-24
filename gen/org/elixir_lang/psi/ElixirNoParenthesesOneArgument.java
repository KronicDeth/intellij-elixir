// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirNoParenthesesOneArgument extends Arguments, MaybeModuleName, QuotableArguments {

  @Nullable
  ElixirMatchedExpression getMatchedExpression();

  @Nullable
  ElixirNoParenthesesKeywords getNoParenthesesKeywords();

  @Nullable
  ElixirNoParenthesesManyArguments getNoParenthesesManyArguments();

  @Nullable
  ElixirNoParenthesesStrict getNoParenthesesStrict();

  @Nullable
  ElixirUnqualifiedNoParenthesesManyArgumentsCall getUnqualifiedNoParenthesesManyArgumentsCall();

  @NotNull
  PsiElement[] arguments();

  boolean isModuleName();

  boolean processDeclarations(PsiScopeProcessor processor, ResolveState state, PsiElement lastParent, PsiElement place);

  @NotNull
  OtpErlangObject[] quoteArguments();

}
