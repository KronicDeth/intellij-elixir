// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public interface ElixirBinaryDigits extends Digits {

  @NotNull
  int base();

  boolean inBase();

  @NotNull
  OtpErlangObject quote();

  @NotNull
  IElementType validElementType();

}
