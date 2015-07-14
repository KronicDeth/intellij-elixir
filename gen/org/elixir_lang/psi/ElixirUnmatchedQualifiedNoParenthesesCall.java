// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirUnmatchedQualifiedNoParenthesesCall extends ElixirUnmatchedExpression, QualifiedNoParenthesesCall {

  @Nullable
  ElixirDoBlock getDoBlock();

  @NotNull
  ElixirDotInfixOperator getDotInfixOperator();

  @NotNull
  ElixirNoParenthesesOneArgument getNoParenthesesOneArgument();

  @NotNull
  ElixirRelativeIdentifier getRelativeIdentifier();

  @NotNull
  ElixirUnmatchedExpression getUnmatchedExpression();

  @NotNull
  OtpErlangObject quote();

}
