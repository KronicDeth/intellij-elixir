// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ElixirHexadecimalWholeNumber extends WholeNumber {

  @NotNull
  List<ElixirHexadecimalDigits> getHexadecimalDigitsList();

  int base();

  @NotNull
  List<Digits> digitsList();

  @NotNull
  OtpErlangObject quote();

}
