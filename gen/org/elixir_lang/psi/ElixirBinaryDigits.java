// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirBinaryDigits extends Digits {

  @Nullable
  PsiElement getInvalidBinaryDigits();

  @Nullable
  PsiElement getValidBinaryDigits();

  boolean inBase();

  @NotNull
  OtpErlangObject quote();

}
