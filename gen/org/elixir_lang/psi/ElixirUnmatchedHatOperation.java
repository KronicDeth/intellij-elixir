// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ElixirUnmatchedHatOperation extends ElixirUnmatchedExpression, InfixOperation {

  @NotNull
  ElixirHatInfixOperator getHatInfixOperator();

  @NotNull
  List<ElixirUnmatchedExpression> getUnmatchedExpressionList();

  @NotNull
  OtpErlangObject quote();

}
