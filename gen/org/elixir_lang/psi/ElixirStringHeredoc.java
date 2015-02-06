// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.intellij.psi.tree.IElementType;

public interface ElixirStringHeredoc extends Heredoc {

  @Nullable
  ElixirHeredocPrefix getHeredocPrefix();

  @NotNull
  List<ElixirStringHeredocLine> getStringHeredocLineList();

  IElementType getFragmentType();

  List<HeredocLine> getHeredocLineList();

  @NotNull
  OtpErlangObject quote();

  @NotNull
  OtpErlangObject quoteBinary(OtpErlangTuple binary);

  @NotNull
  OtpErlangObject quoteEmpty();

  @NotNull
  OtpErlangObject quoteLiteral(List<Integer> codePointList);

}
