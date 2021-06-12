// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiReference;

public interface ElixirUnmatchedAtOperation extends ElixirUnmatchedExpression, AtNonNumericOperation {

  @NotNull
  ElixirAtPrefixOperator getAtPrefixOperator();

  @Nullable
  ElixirUnmatchedExpression getUnmatchedExpression();

  @Nullable PsiReference getReference();

  @NotNull String moduleAttributeName();

  @Nullable Quotable operand();

  @NotNull Operator operator();

  @NotNull OtpErlangObject quote();

}
