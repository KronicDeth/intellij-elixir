// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ElixirMatchedHatOperation extends InfixOperation {

  @NotNull
  List<ElixirAlias> getAliasList();

  @NotNull
  List<ElixirAtNumericOperation> getAtNumericOperationList();

  @NotNull
  List<ElixirAtom> getAtomList();

  @NotNull
  List<ElixirAtomKeyword> getAtomKeywordList();

  @NotNull
  List<ElixirCaptureNumericOperation> getCaptureNumericOperationList();

  @NotNull
  List<ElixirCharListHeredoc> getCharListHeredocList();

  @NotNull
  List<ElixirCharListLine> getCharListLineList();

  @NotNull
  List<ElixirCharToken> getCharTokenList();

  @NotNull
  List<ElixirEmptyBlock> getEmptyBlockList();

  @NotNull
  ElixirHatInfixOperator getHatInfixOperator();

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

  @Nullable
  ElixirMatchedDotOperation getMatchedDotOperation();

  @Nullable
  ElixirMatchedHatOperation getMatchedHatOperation();

  @NotNull
  List<ElixirMatchedNonNumericAtOperation> getMatchedNonNumericAtOperationList();

  @NotNull
  List<ElixirMatchedNonNumericCaptureOperation> getMatchedNonNumericCaptureOperationList();

  @NotNull
  List<ElixirMatchedNonNumericUnaryOperation> getMatchedNonNumericUnaryOperationList();

  @Nullable
  ElixirNoParenthesesManyArgumentsQualifiedCall getNoParenthesesManyArgumentsQualifiedCall();

  @Nullable
  ElixirNoParenthesesManyArgumentsUnqualifiedCall getNoParenthesesManyArgumentsUnqualifiedCall();

  @NotNull
  List<ElixirNoParenthesesNoArgumentsCall> getNoParenthesesNoArgumentsCallList();

  @NotNull
  List<ElixirNoParenthesesNoArgumentsUnqualifiedCallOrVariable> getNoParenthesesNoArgumentsUnqualifiedCallOrVariableList();

  @NotNull
  List<ElixirNumber> getNumberList();

  @NotNull
  List<ElixirQualifiedAlias> getQualifiedAliasList();

  @NotNull
  List<ElixirStringHeredoc> getStringHeredocList();

  @NotNull
  List<ElixirStringLine> getStringLineList();

  @NotNull
  List<ElixirUnaryNumericOperation> getUnaryNumericOperationList();

  @NotNull
  OtpErlangObject quote();

}
