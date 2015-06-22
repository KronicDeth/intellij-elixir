// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ElixirBlockItem extends Quotable {

  @NotNull
  ElixirBlockIdentifier getBlockIdentifier();

  @NotNull
  List<ElixirEndOfExpression> getEndOfExpressionList();

  @Nullable
  ElixirStab getStab();

  @NotNull
  OtpErlangObject quote();

}
