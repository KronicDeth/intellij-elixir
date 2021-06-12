// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirUnmatchedAtUnqualifiedBracketOperation extends ElixirUnmatchedExpression, AtUnqualifiedBracketOperation {

  @NotNull
  ElixirAtPrefixOperator getAtPrefixOperator();

  @NotNull
  ElixirBracketArguments getBracketArguments();

  @NotNull OtpErlangObject quote();

}
