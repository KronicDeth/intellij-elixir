// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

public interface ElixirStabOperation extends InfixOperation {

  @NotNull
  ElixirStabBody getStabBody();

  @NotNull
  ElixirStabInfixOperator getStabInfixOperator();

  @NotNull
  ElixirStabSignature getStabSignature();

  @NotNull
  OtpErlangObject quote();

}
