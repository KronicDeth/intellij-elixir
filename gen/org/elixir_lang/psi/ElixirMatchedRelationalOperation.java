// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirMatchedRelationalOperation extends ElixirMatchedExpression, InfixOperation {

  @NotNull
  List<ElixirMatchedExpression> getMatchedExpressionList();

  @NotNull
  ElixirRelationalInfixOperator getRelationalInfixOperator();

  @NotNull
  OtpErlangObject quote();

}
