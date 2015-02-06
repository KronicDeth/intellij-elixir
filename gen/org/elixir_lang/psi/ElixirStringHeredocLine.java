// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.tree.IElementType;

public interface ElixirStringHeredocLine extends HeredocLine {

  @NotNull
  ElixirHeredocLinePrefix getHeredocLinePrefix();

  @NotNull
  ElixirInterpolatedStringBody getInterpolatedStringBody();

  IElementType getFragmentType();

  InterpolatedBody getInterpolatedBody();

  @NotNull
  OtpErlangObject quote(int prefixLength);

}
