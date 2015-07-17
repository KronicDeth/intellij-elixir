// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirStabOperation extends Quotable {

  @Nullable
  ElixirStabBody getStabBody();

  @NotNull
  ElixirStabInfixOperator getStabInfixOperator();

  @Nullable
  ElixirStabNoParenthesesSignature getStabNoParenthesesSignature();

  @Nullable
  ElixirStabParenthesesSignature getStabParenthesesSignature();

  @NotNull
  OtpErlangObject quote();

}
