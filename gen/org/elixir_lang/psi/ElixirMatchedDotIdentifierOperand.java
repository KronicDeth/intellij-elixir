// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirMatchedDotIdentifierOperand extends Quotable {

  @Nullable
  ElixirAtomKeyword getAtomKeyword();

  @Nullable
  ElixirCharListHeredoc getCharListHeredoc();

  @Nullable
  ElixirCharListLine getCharListLine();

  @Nullable
  ElixirStringHeredoc getStringHeredoc();

  @Nullable
  ElixirStringLine getStringLine();

  @NotNull
  OtpErlangObject quote();

}
