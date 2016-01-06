// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirUnmatchedAtNonNumericOperation extends ElixirUnmatchedExpression, AtNonNumericOperation {

  @NotNull
  ElixirAtPrefixOperator getAtPrefixOperator();

  @Nullable
  ElixirUnmatchedExpression getUnmatchedExpression();

  @Nullable
  PsiReference getReference();

  @NotNull
  String moduleAttributeName();

  @NotNull
  OtpErlangObject quote();

}
