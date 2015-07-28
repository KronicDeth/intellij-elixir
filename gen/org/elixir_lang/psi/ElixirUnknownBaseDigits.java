// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirUnknownBaseDigits extends Digits {

  @NotNull
  int base();

  boolean inBase();

  @NotNull
  OtpErlangObject quote();

  @Nullable
  IElementType validElementType();

}
