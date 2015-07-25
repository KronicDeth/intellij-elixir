// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

public interface ElixirUnmatchedQualifiedAlias extends ElixirUnmatchedExpression, QualifiedAlias {

  @NotNull
  ElixirAlias getAlias();

  @NotNull
  ElixirDotInfixOperator getDotInfixOperator();

  @NotNull
  ElixirUnmatchedExpression getUnmatchedExpression();

  @NotNull
  OtpErlangObject quote();

}
