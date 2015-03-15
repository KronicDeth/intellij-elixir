// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ElixirUnknownBaseWholeNumber extends ElixirNumber, WholeNumber {

  @NotNull
  List<ElixirUnknownBaseDigits> getUnknownBaseDigitsList();

  @NotNull
  PsiElement getUnknownWholeNumberBase();

  @NotNull
  int base();

  @NotNull
  List<Digits> digitsList();

  @NotNull
  OtpErlangObject quote();

}
