// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;

public interface ElixirStabOperation extends Quotable {

  @Nullable
  ElixirStabBody getStabBody();

  @NotNull
  ElixirStabInfixOperator getStabInfixOperator();

  @Nullable
  ElixirStabNoParenthesesSignature getStabNoParenthesesSignature();

  @Nullable
  ElixirStabParenthesesSignature getStabParenthesesSignature();

  @Nullable Quotable leftOperand();

  @NotNull Operator operator();

  boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place);

  @NotNull OtpErlangObject quote();

  @Nullable Quotable rightOperand();

}
