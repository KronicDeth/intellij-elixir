// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public interface ElixirMatchedQualifiedBracketOperation extends ElixirMatchedExpression, QualifiedBracketOperation {

  @NotNull
  ElixirBracketArguments getBracketArguments();

  @NotNull
  ElixirDotInfixOperator getDotInfixOperator();

  @NotNull
  ElixirMatchedExpression getMatchedExpression();

  @NotNull
  ElixirRelativeIdentifier getRelativeIdentifier();

  @NotNull
  PsiElement qualifier();

  @NotNull
  OtpErlangObject quote();

}
