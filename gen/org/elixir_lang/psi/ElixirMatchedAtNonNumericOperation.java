// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirMatchedAtNonNumericOperation extends ElixirMatchedExpression, ModuleAttributeNameable, PrefixOperation {

  @NotNull
  ElixirAtPrefixOperator getAtPrefixOperator();

  @Nullable
  ElixirMatchedExpression getMatchedExpression();

  @Nullable
  PsiReference getReference();

  @NotNull
  String moduleAttributeName();

  @NotNull
  OtpErlangObject quote();

}
