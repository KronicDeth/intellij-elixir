// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirMatchedMultiplicationOperation extends InfixOperation {

  @NotNull
  List<ElixirAlias> getAliasList();

  @NotNull
  List<ElixirAtNumericOperation> getAtNumericOperationList();

  @NotNull
  List<ElixirAtom> getAtomList();

  @NotNull
  List<ElixirAtomKeyword> getAtomKeywordList();

  @NotNull
  List<ElixirBinaryWholeNumber> getBinaryWholeNumberList();

  @NotNull
  List<ElixirCaptureNumericOperation> getCaptureNumericOperationList();

  @NotNull
  List<ElixirCharListHeredoc> getCharListHeredocList();

  @NotNull
  List<ElixirCharListLine> getCharListLineList();

  @NotNull
  List<ElixirCharToken> getCharTokenList();

  @NotNull
  List<ElixirDecimalFloat> getDecimalFloatList();

  @NotNull
  List<ElixirDecimalWholeNumber> getDecimalWholeNumberList();

  @NotNull
  List<ElixirEmptyBlock> getEmptyBlockList();

  @NotNull
  List<ElixirHexadecimalWholeNumber> getHexadecimalWholeNumberList();

  @NotNull
  List<ElixirInterpolatedCharListSigilHeredoc> getInterpolatedCharListSigilHeredocList();

  @NotNull
  List<ElixirInterpolatedCharListSigilLine> getInterpolatedCharListSigilLineList();

  @NotNull
  List<ElixirInterpolatedRegexHeredoc> getInterpolatedRegexHeredocList();

  @NotNull
  List<ElixirInterpolatedRegexLine> getInterpolatedRegexLineList();

  @NotNull
  List<ElixirInterpolatedSigilHeredoc> getInterpolatedSigilHeredocList();

  @NotNull
  List<ElixirInterpolatedSigilLine> getInterpolatedSigilLineList();

  @NotNull
  List<ElixirInterpolatedStringSigilHeredoc> getInterpolatedStringSigilHeredocList();

  @NotNull
  List<ElixirInterpolatedStringSigilLine> getInterpolatedStringSigilLineList();

  @NotNull
  List<ElixirInterpolatedWordsHeredoc> getInterpolatedWordsHeredocList();

  @NotNull
  List<ElixirInterpolatedWordsLine> getInterpolatedWordsLineList();

  @NotNull
  List<ElixirList> getListList();

  @NotNull
  List<ElixirLiteralCharListSigilHeredoc> getLiteralCharListSigilHeredocList();

  @NotNull
  List<ElixirLiteralCharListSigilLine> getLiteralCharListSigilLineList();

  @NotNull
  List<ElixirLiteralRegexHeredoc> getLiteralRegexHeredocList();

  @NotNull
  List<ElixirLiteralRegexLine> getLiteralRegexLineList();

  @NotNull
  List<ElixirLiteralSigilHeredoc> getLiteralSigilHeredocList();

  @NotNull
  List<ElixirLiteralSigilLine> getLiteralSigilLineList();

  @NotNull
  List<ElixirLiteralStringSigilHeredoc> getLiteralStringSigilHeredocList();

  @NotNull
  List<ElixirLiteralStringSigilLine> getLiteralStringSigilLineList();

  @NotNull
  List<ElixirLiteralWordsHeredoc> getLiteralWordsHeredocList();

  @NotNull
  List<ElixirLiteralWordsLine> getLiteralWordsLineList();

  @NotNull
  List<ElixirMatchedCallOperation> getMatchedCallOperationList();

  @NotNull
  List<ElixirMatchedCaptureNonNumericOperation> getMatchedCaptureNonNumericOperationList();

  @NotNull
  List<ElixirMatchedDotOperation> getMatchedDotOperationList();

  @NotNull
  List<ElixirMatchedHatOperation> getMatchedHatOperationList();

  @Nullable
  ElixirMatchedMultiplicationOperation getMatchedMultiplicationOperation();

  @NotNull
  List<ElixirMatchedNonNumericAtOperation> getMatchedNonNumericAtOperationList();

  @NotNull
  List<ElixirMatchedUnaryNonNumericOperation> getMatchedUnaryNonNumericOperationList();

  @NotNull
  ElixirMultiplicationInfixOperator getMultiplicationInfixOperator();

  @NotNull
  List<ElixirNoParenthesesNoArgumentsUnqualifiedCallOrVariable> getNoParenthesesNoArgumentsUnqualifiedCallOrVariableList();

  @NotNull
  List<ElixirOctalWholeNumber> getOctalWholeNumberList();

  @NotNull
  List<ElixirStringHeredoc> getStringHeredocList();

  @NotNull
  List<ElixirStringLine> getStringLineList();

  @NotNull
  List<ElixirUnaryNumericOperation> getUnaryNumericOperationList();

  @NotNull
  List<ElixirUnknownBaseWholeNumber> getUnknownBaseWholeNumberList();

  @NotNull
  OtpErlangObject quote();

}
