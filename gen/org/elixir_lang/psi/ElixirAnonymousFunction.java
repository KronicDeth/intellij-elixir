// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.NavigatablePsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.util.concurrency.annotations.RequiresReadLock;

public interface ElixirAnonymousFunction extends NavigatablePsiElement, Quotable {

  @NotNull
  List<ElixirEndOfExpression> getEndOfExpressionList();

  @NotNull
  ElixirStab getStab();

  @RequiresReadLock
  @NotNull OtpErlangObject quote();

}
