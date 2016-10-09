// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirIdentifier extends Quotable {

  @Nullable
  ItemPresentation getPresentation();

  @NotNull
  OtpErlangObject quote();

}
