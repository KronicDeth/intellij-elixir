// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirInterpolatedSigilHeredocLine extends HeredocLine {

  @Nullable
  ElixirEscapedEOL getEscapedEOL();

  @NotNull
  ElixirHeredocLinePrefix getHeredocLinePrefix();

  @NotNull
  ElixirInterpolatedSigilHeredocLineBody getInterpolatedSigilHeredocLineBody();

  Body getBody();

  @NotNull OtpErlangObject quote(@NotNull Heredoc heredoc, int prefixLength);

}
