// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirMatchedAtNumericBracketOperation extends ElixirMatchedExpression, AtNumericBracketOperation {

  @NotNull
  ElixirAtPrefixOperator getAtPrefixOperator();

  @Nullable
  ElixirBinaryWholeNumber getBinaryWholeNumber();

  @NotNull
  ElixirBracketArguments getBracketArguments();

  @Nullable
  ElixirCharToken getCharToken();

  @Nullable
  ElixirDecimalFloat getDecimalFloat();

  @Nullable
  ElixirDecimalWholeNumber getDecimalWholeNumber();

  @Nullable
  ElixirHexadecimalWholeNumber getHexadecimalWholeNumber();

  @Nullable
  ElixirOctalWholeNumber getOctalWholeNumber();

  @Nullable
  ElixirUnknownBaseWholeNumber getUnknownBaseWholeNumber();

  @NotNull OtpErlangObject quote();

}
