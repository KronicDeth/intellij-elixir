// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirUnqualifiedNoParenthesesManyArgumentsCall extends Call {

  @Nullable
  ElixirNoParenthesesManyArguments getNoParenthesesManyArguments();

  @NotNull
  ElixirNoParenthesesManyArgumentsUnqualifiedIdentifier getNoParenthesesManyArgumentsUnqualifiedIdentifier();

  @Nullable
  ElixirNoParenthesesStrict getNoParenthesesStrict();

  @NotNull
  QuotableArguments getArguments();

  @NotNull
  Quotable getIdentifier();

  @NotNull
  OtpErlangObject quote();

  @NotNull
  OtpErlangObject[] quoteArguments();

  OtpErlangObject quoteIdentifier();

}
