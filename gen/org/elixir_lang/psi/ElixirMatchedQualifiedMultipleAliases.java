// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

public interface ElixirMatchedQualifiedMultipleAliases extends ElixirMatchedExpression, QualifiedMultipleAliases {

  @NotNull
  ElixirDotInfixOperator getDotInfixOperator();

  @NotNull
  ElixirMatchedExpression getMatchedExpression();

  @NotNull
  ElixirMultipleAliases getMultipleAliases();

  @NotNull
  OtpErlangObject quote();

}
