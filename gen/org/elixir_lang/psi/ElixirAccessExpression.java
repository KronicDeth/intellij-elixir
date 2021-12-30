// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirAccessExpression extends MaybeModuleName, Quotable {

  @Nullable
  ElixirAlias getAlias();

  @Nullable
  ElixirAnonymousFunction getAnonymousFunction();

  @Nullable
  ElixirAtom getAtom();

  @Nullable
  ElixirAtomKeyword getAtomKeyword();

  @Nullable
  ElixirBinaryWholeNumber getBinaryWholeNumber();

  @Nullable
  ElixirBitString getBitString();

  @Nullable
  ElixirCaptureNumericOperation getCaptureNumericOperation();

  @Nullable
  ElixirCharToken getCharToken();

  @Nullable
  ElixirDecimalFloat getDecimalFloat();

  @Nullable
  ElixirDecimalWholeNumber getDecimalWholeNumber();

  @Nullable
  ElixirHeredoc getHeredoc();

  @Nullable
  ElixirHexadecimalWholeNumber getHexadecimalWholeNumber();

  @Nullable
  ElixirInterpolatedSigilHeredoc getInterpolatedSigilHeredoc();

  @Nullable
  ElixirInterpolatedSigilLine getInterpolatedSigilLine();

  @Nullable
  ElixirLine getLine();

  @Nullable
  ElixirList getList();

  @Nullable
  ElixirLiteralSigilHeredoc getLiteralSigilHeredoc();

  @Nullable
  ElixirLiteralSigilLine getLiteralSigilLine();

  @Nullable
  ElixirMapOperation getMapOperation();

  @Nullable
  ElixirOctalWholeNumber getOctalWholeNumber();

  @Nullable
  ElixirParentheticalStab getParentheticalStab();

  @Nullable
  ElixirStructOperation getStructOperation();

  @Nullable
  ElixirTuple getTuple();

  @Nullable
  ElixirUnknownBaseWholeNumber getUnknownBaseWholeNumber();

  boolean isModuleName();

  @NotNull OtpErlangObject quote();

}
