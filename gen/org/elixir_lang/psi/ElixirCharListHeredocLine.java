// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public interface ElixirCharListHeredocLine extends PsiElement {

  @NotNull
  ElixirCharListHeredocLineWhitespace getCharListHeredocLineWhitespace();

  @NotNull
  ElixirInterpolatedCharListBody getInterpolatedCharListBody();

  @NotNull
  OtpErlangObject quote(int prefixLength);

}
