// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirNoParenthesesManyArgumentsCall extends Quotable {

  @Nullable
  ElixirNoParenthesesManyArguments getNoParenthesesManyArguments();

  @Nullable
  ElixirNoParenthesesManyArgumentsUnqualifiedIdentifier getNoParenthesesManyArgumentsUnqualifiedIdentifier();

  @Nullable
  ElixirNoParenthesesQualifiedIdentifier getNoParenthesesQualifiedIdentifier();

  @Nullable
  ElixirNoParenthesesStrict getNoParenthesesStrict();

  @NotNull
  QuotableArguments getArguments();

  @NotNull
  Quotable getIdentifier();

  @NotNull
  OtpErlangObject quote();

}
