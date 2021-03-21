// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirHexadecimalWholeNumber extends WholeNumber {

  @NotNull
  List<ElixirHexadecimalDigits> getHexadecimalDigitsList();

  int base();

  @NotNull List<Digits> digitsList();

  @NotNull OtpErlangObject quote();

}
