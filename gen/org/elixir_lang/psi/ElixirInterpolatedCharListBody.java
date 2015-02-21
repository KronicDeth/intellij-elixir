// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;

public interface ElixirInterpolatedCharListBody extends InterpolatedCharList, InterpolatedBody {

  @NotNull
  List<ElixirEscapedCharacter> getEscapedCharacterList();

  @NotNull
  List<ElixirHexadecimalEscapeSequence> getHexadecimalEscapeSequenceList();

  @NotNull
  List<ElixirInterpolation> getInterpolationList();

  @NotNull
  List<Integer> addEscapedCharacterCodePoints(List<Integer> codePointList, ASTNode child);

  @NotNull
  List<Integer> addFragmentCodePoints(List<Integer> codePointList, ASTNode child);

  @NotNull
  List<Integer> addHexadecimalEscapeSequenceCodePoints(List<Integer> codePointList, ASTNode child);

  IElementType getFragmentType();

  @NotNull
  OtpErlangObject quote();

  @NotNull
  OtpErlangObject quoteBinary(OtpErlangTuple binary);

  @NotNull
  OtpErlangObject quoteEmpty();

  @NotNull
  OtpErlangObject quoteLiteral(List<Integer> codePointList);

}
