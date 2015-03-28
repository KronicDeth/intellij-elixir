// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ElixirBinaryWholeNumber extends WholeNumber {

  @NotNull
  List<ElixirBinaryDigits> getBinaryDigitsList();

  @NotNull
  int base();

  @NotNull
  List<Digits> digitsList();

  @NotNull
  OtpErlangObject quote();

}
