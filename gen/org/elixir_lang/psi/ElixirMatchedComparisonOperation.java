// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ElixirMatchedComparisonOperation extends ElixirMatchedExpression, InfixOperation {

  @NotNull
  ElixirComparisonInfixOperator getComparisonInfixOperator();

  @NotNull
  List<ElixirMatchedExpression> getMatchedExpressionList();

  @NotNull
  Quotable leftOperand();

  @NotNull
  Operator operator();

  @NotNull
  OtpErlangObject quote();

  @NotNull
  Quotable rightOperand();

}
