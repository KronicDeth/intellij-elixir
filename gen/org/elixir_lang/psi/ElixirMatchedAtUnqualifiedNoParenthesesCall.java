// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

public interface ElixirMatchedAtUnqualifiedNoParenthesesCall extends ElixirMatchedExpression, Quotable {

  @NotNull
  ElixirAtPrefixOperator getAtPrefixOperator();

  @NotNull
  ElixirMatchedNoParenthesesArguments getMatchedNoParenthesesArguments();

  @NotNull
  OtpErlangObject quote();

}
