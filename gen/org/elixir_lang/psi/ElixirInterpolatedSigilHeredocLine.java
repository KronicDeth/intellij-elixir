// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirInterpolatedSigilHeredocLine extends HeredocLine {

  @NotNull
  ElixirHeredocLinePrefix getHeredocLinePrefix();

  @NotNull
  ElixirInterpolatedSigilBody getInterpolatedSigilBody();

  Body getBody();

  @NotNull
  OtpErlangObject quote(Heredoc heredoc, int prefixLength);

}
