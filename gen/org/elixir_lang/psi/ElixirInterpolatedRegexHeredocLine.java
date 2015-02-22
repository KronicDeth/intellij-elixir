// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public interface ElixirInterpolatedRegexHeredocLine extends RegexFragmented, HeredocLine {

  @NotNull
  ElixirHeredocLinePrefix getHeredocLinePrefix();

  @NotNull
  ElixirInterpolatedRegexBody getInterpolatedRegexBody();

  IElementType getFragmentType();

  InterpolatedBody getInterpolatedBody();

  @NotNull
  OtpErlangObject quote(Heredoc heredoc, int prefixLength);

}
