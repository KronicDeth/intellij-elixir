// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

public interface ElixirMatchedUnqualifiedCallOperation extends ElixirMatchedExpression, Quotable {

  @NotNull
  ElixirMatchedCallArguments getMatchedCallArguments();

  @NotNull
  ElixirVariable getVariable();

  @NotNull
  OtpErlangObject quote();

}
