// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public interface ElixirInterpolatedCharListHeredocLine extends CharListFragmented, HeredocLine {

  @NotNull
  ElixirHeredocLinePrefix getHeredocLinePrefix();

  @NotNull
  ElixirInterpolatedCharListBody getInterpolatedCharListBody();

  IElementType getFragmentType();

  InterpolatedBody getInterpolatedBody();

  @NotNull
  OtpErlangObject quote(Heredoc heredoc, int prefixLength);

}
