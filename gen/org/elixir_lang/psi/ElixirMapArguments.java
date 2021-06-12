// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirMapArguments extends Quotable {

  @Nullable
  ElixirMapConstructionArguments getMapConstructionArguments();

  @Nullable
  ElixirMapUpdateArguments getMapUpdateArguments();

  @NotNull OtpErlangObject quote();

}
