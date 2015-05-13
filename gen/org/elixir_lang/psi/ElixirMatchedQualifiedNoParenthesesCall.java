// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirMatchedQualifiedNoParenthesesCall extends ElixirMatchedExpression, Quotable {

  @NotNull
  ElixirDotInfixOperator getDotInfixOperator();

  @NotNull
  ElixirMatchedExpression getMatchedExpression();

  @NotNull
  ElixirMatchedNoParenthesesArguments getMatchedNoParenthesesArguments();

  @NotNull
  ElixirRelativeIdentifier getRelativeIdentifier();

  @NotNull
  OtpErlangObject quote();

}
