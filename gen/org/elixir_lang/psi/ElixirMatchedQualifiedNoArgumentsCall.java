// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirMatchedQualifiedNoArgumentsCall extends ElixirMatchedExpression, MatchedCall, QualifiedNoArgumentsCall {

  @NotNull
  ElixirDotInfixOperator getDotInfixOperator();

  @NotNull
  ElixirMatchedExpression getMatchedExpression();

  @NotNull
  ElixirRelativeIdentifier getRelativeIdentifier();

  @Nullable
  ElixirDoBlock getDoBlock();

  @NotNull
  OtpErlangObject quote();

}
