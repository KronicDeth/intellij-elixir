// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.operation.When;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirStabParenthesesSignature extends Quotable, When {

  @Nullable
  ElixirEmptyParentheses getEmptyParentheses();

  @NotNull
  ElixirParenthesesArguments getParenthesesArguments();

  @Nullable
  ElixirUnmatchedExpression getUnmatchedExpression();

  @Nullable
  ElixirUnqualifiedNoParenthesesManyArgumentsCall getUnqualifiedNoParenthesesManyArgumentsCall();

  @Nullable
  ElixirWhenInfixOperator getWhenInfixOperator();

  //WARNING: getNameIdentifier(...) is skipped
  //matching getNameIdentifier(ElixirStabParenthesesSignature, ...)
  //methods are not found in ElixirPsiImplUtil

  @Nullable Quotable leftOperand();

  @NotNull Operator operator();

  @NotNull OtpErlangObject quote();

  @Nullable Quotable rightOperand();

}
