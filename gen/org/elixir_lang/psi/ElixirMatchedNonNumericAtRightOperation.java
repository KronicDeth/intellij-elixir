// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirMatchedNonNumericAtRightOperation extends PrefixOperation {

  @NotNull
  ElixirAtPrefixOperator getAtPrefixOperator();

  @Nullable
  ElixirMatchedNonNumericAtRightOperation getMatchedNonNumericAtRightOperation();

  @Nullable
  ElixirMatchedNonNumericCaptureRightOperation getMatchedNonNumericCaptureRightOperation();

  @Nullable
  ElixirMatchedNonNumericUnaryRightOperation getMatchedNonNumericUnaryRightOperation();

  @Nullable
  ElixirNoParenthesesManyArgumentsCall getNoParenthesesManyArgumentsCall();

  @NotNull
  OtpErlangObject quote();

}
