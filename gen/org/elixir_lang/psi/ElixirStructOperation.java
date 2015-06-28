// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirStructOperation extends Quotable {

  @Nullable
  ElixirAlias getAlias();

  @Nullable
  ElixirAtom getAtom();

  @NotNull
  ElixirMapArguments getMapArguments();

  @NotNull
  ElixirMapPrefixOperator getMapPrefixOperator();

  @Nullable
  ElixirMatchedExpression getMatchedExpression();

  @Nullable
  ElixirVariable getVariable();

  @NotNull
  OtpErlangObject quote();

}
