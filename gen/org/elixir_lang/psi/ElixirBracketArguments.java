// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirBracketArguments extends Quotable {

  @Nullable
  ElixirEmptyParentheses getEmptyParentheses();

  @Nullable
  ElixirKeywords getKeywords();

  @Nullable
  ElixirUnmatchedExpression getUnmatchedExpression();

  @NotNull OtpErlangObject quote();

}
