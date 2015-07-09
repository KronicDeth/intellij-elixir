// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

public interface ElixirUnmatchedAtUnqualifiedBracketOperation extends ElixirUnmatchedExpression, AtUnqualifiedBracketOperation {

  @NotNull
  ElixirAtPrefixOperator getAtPrefixOperator();

  @NotNull
  ElixirBracketArguments getBracketArguments();

  @NotNull
  OtpErlangObject quote();

}
