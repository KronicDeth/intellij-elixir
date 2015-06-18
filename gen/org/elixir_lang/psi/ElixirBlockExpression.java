// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirBlockExpression extends Quotable {

  @Nullable
  ElixirBlockNoParenthesesCall getBlockNoParenthesesCall();

  @NotNull
  ElixirDoBlock getDoBlock();

  @Nullable
  ElixirMatchedExpression getMatchedExpression();

  @NotNull
  OtpErlangObject quote();

}
