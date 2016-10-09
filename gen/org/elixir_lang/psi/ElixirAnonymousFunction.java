// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.NavigatablePsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ElixirAnonymousFunction extends NavigatablePsiElement, Quotable {

  @NotNull
  List<ElixirEndOfExpression> getEndOfExpressionList();

  @NotNull
  ElixirStab getStab();

  @NotNull
  OtpErlangObject quote();

}
