// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirCaptureNumericOperation extends PrefixOperation {

  @Nullable
  ElixirBinaryWholeNumber getBinaryWholeNumber();

  @NotNull
  ElixirCapturePrefixOperator getCapturePrefixOperator();

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

  @NotNull
  OtpErlangObject quote();

}
