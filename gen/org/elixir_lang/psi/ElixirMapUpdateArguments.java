// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ElixirMapUpdateArguments extends Quotable {

  @Nullable
  ElixirAlias getAlias();

  @Nullable
  ElixirAtom getAtom();

  @NotNull
  List<ElixirMatchedExpression> getMatchedExpressionList();

  @NotNull
  ElixirPipeInfixOperator getPipeInfixOperator();

  @NotNull
  OtpErlangObject quote();

}
