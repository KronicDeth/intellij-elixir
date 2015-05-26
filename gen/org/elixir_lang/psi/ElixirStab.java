// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ElixirStab extends Quotable {

  @NotNull
  List<ElixirEndOfExpression> getEndOfExpressionList();

  @Nullable
  ElixirStabBody getStabBody();

  @NotNull
  List<ElixirStabOperation> getStabOperationList();

  @NotNull
  OtpErlangObject quote();

}
