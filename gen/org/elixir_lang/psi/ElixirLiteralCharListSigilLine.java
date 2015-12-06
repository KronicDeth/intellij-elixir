// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ElixirLiteralCharListSigilLine extends CharListFragmented, SigilLine {

  @NotNull
  ElixirLiteralCharListBody getLiteralCharListBody();

  @NotNull
  ElixirSigilModifiers getSigilModifiers();

  @NotNull
  List<Integer> addEscapedCharacterCodePoints(List<Integer> codePointList, ASTNode child);

  @NotNull
  List<Integer> addFragmentCodePoints(List<Integer> codePointList, ASTNode child);

  @NotNull
  List<Integer> addHexadecimalEscapeSequenceCodePoints(List<Integer> codePointList, ASTNode child);

  Body getBody();

  IElementType getFragmentType();

  @NotNull
  OtpErlangObject quote();

  @NotNull
  OtpErlangObject quoteBinary(OtpErlangTuple binary);

  @NotNull
  OtpErlangObject quoteEmpty();

  @NotNull
  OtpErlangObject quoteLiteral(List<Integer> codePointList);

  char sigilName();

  char terminator();

}
