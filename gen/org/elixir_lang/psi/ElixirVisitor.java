// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirVisitor extends PsiElementVisitor {

  public void visitAccessExpression(@NotNull ElixirAccessExpression o) {
    visitMaybeModuleName(o);
    // visitQuotable(o);
  }

  public void visitAdditionInfixOperator(@NotNull ElixirAdditionInfixOperator o) {
    visitOperator(o);
  }

  public void visitAlias(@NotNull ElixirAlias o) {
    visitNamedElement(o);
    // visitQualifiableAlias(o);
    // visitQuotable(o);
  }

  public void visitAndInfixOperator(@NotNull ElixirAndInfixOperator o) {
    visitOperator(o);
  }

  public void visitAnonymousFunction(@NotNull ElixirAnonymousFunction o) {
    visitQuotable(o);
  }

  public void visitArrowInfixOperator(@NotNull ElixirArrowInfixOperator o) {
    visitOperator(o);
  }

  public void visitAssociations(@NotNull ElixirAssociations o) {
    visitQuotable(o);
  }

  public void visitAssociationsBase(@NotNull ElixirAssociationsBase o) {
    visitQuotable(o);
  }

  public void visitAtNumericOperation(@NotNull ElixirAtNumericOperation o) {
    visitPrefixOperation(o);
  }

  public void visitAtPrefixOperator(@NotNull ElixirAtPrefixOperator o) {
    visitOperator(o);
  }

  public void visitAtom(@NotNull ElixirAtom o) {
    visitQuotable(o);
  }

  public void visitAtomKeyword(@NotNull ElixirAtomKeyword o) {
    visitQuotable(o);
  }

  public void visitBinaryDigits(@NotNull ElixirBinaryDigits o) {
    visitDigits(o);
  }

  public void visitBinaryWholeNumber(@NotNull ElixirBinaryWholeNumber o) {
    visitWholeNumber(o);
  }

  public void visitBitString(@NotNull ElixirBitString o) {
    visitQuotable(o);
  }

  public void visitBlockIdentifier(@NotNull ElixirBlockIdentifier o) {
    visitQuotable(o);
  }

  public void visitBlockItem(@NotNull ElixirBlockItem o) {
    visitQuotable(o);
  }

  public void visitBlockList(@NotNull ElixirBlockList o) {
    visitQuotableArguments(o);
  }

  public void visitBracketArguments(@NotNull ElixirBracketArguments o) {
    visitQuotable(o);
  }

  public void visitCaptureNumericOperation(@NotNull ElixirCaptureNumericOperation o) {
    visitPrefixOperation(o);
  }

  public void visitCapturePrefixOperator(@NotNull ElixirCapturePrefixOperator o) {
    visitOperator(o);
  }

  public void visitCharListHeredoc(@NotNull ElixirCharListHeredoc o) {
    visitHeredoc(o);
    // visitInterpolatedCharList(o);
    // visitQuote(o);
  }

  public void visitCharListHeredocLine(@NotNull ElixirCharListHeredocLine o) {
    visitHeredocLine(o);
  }

  public void visitCharListLine(@NotNull ElixirCharListLine o) {
    visitAtomable(o);
    // visitInterpolatedCharList(o);
    // visitLine(o);
    // visitQuotable(o);
  }

  public void visitCharToken(@NotNull ElixirCharToken o) {
    visitQuotable(o);
  }

  public void visitComparisonInfixOperator(@NotNull ElixirComparisonInfixOperator o) {
    visitOperator(o);
  }

  public void visitContainerAssociationOperation(@NotNull ElixirContainerAssociationOperation o) {
    visitAssociationOperation(o);
  }

  public void visitDecimalDigits(@NotNull ElixirDecimalDigits o) {
    visitDigits(o);
  }

  public void visitDecimalFloat(@NotNull ElixirDecimalFloat o) {
    visitQuotable(o);
  }

  public void visitDecimalFloatExponent(@NotNull ElixirDecimalFloatExponent o) {
    visitPsiElement(o);
  }

  public void visitDecimalFloatExponentSign(@NotNull ElixirDecimalFloatExponentSign o) {
    visitPsiElement(o);
  }

  public void visitDecimalFloatFractional(@NotNull ElixirDecimalFloatFractional o) {
    visitPsiElement(o);
  }

  public void visitDecimalFloatIntegral(@NotNull ElixirDecimalFloatIntegral o) {
    visitPsiElement(o);
  }

  public void visitDecimalWholeNumber(@NotNull ElixirDecimalWholeNumber o) {
    visitWholeNumber(o);
  }

  public void visitDoBlock(@NotNull ElixirDoBlock o) {
    visitQuotableArguments(o);
  }

  public void visitDotInfixOperator(@NotNull ElixirDotInfixOperator o) {
    visitOperator(o);
  }

  public void visitEmptyParentheses(@NotNull ElixirEmptyParentheses o) {
    visitQuotable(o);
  }

  public void visitEnclosedHexadecimalEscapeSequence(@NotNull ElixirEnclosedHexadecimalEscapeSequence o) {
    visitEscapedHexadecimalDigits(o);
  }

  public void visitEndOfExpression(@NotNull ElixirEndOfExpression o) {
    visitUnquoted(o);
  }

  public void visitEscapedCharacter(@NotNull ElixirEscapedCharacter o) {
    visitEscapeSequence(o);
  }

  public void visitEscapedEOL(@NotNull ElixirEscapedEOL o) {
    visitEscapeSequence(o);
  }

  public void visitHeredocLinePrefix(@NotNull ElixirHeredocLinePrefix o) {
    visitPsiElement(o);
  }

  public void visitHeredocPrefix(@NotNull ElixirHeredocPrefix o) {
    visitPsiElement(o);
  }

  public void visitHexadecimalDigits(@NotNull ElixirHexadecimalDigits o) {
    visitDigits(o);
  }

  public void visitHexadecimalEscapePrefix(@NotNull ElixirHexadecimalEscapePrefix o) {
    visitPsiElement(o);
  }

  public void visitHexadecimalWholeNumber(@NotNull ElixirHexadecimalWholeNumber o) {
    visitWholeNumber(o);
  }

  public void visitInInfixOperator(@NotNull ElixirInInfixOperator o) {
    visitOperator(o);
  }

  public void visitInMatchInfixOperator(@NotNull ElixirInMatchInfixOperator o) {
    visitOperator(o);
  }

  public void visitInterpolatedCharListBody(@NotNull ElixirInterpolatedCharListBody o) {
    visitBody(o);
  }

  public void visitInterpolatedCharListHeredocLine(@NotNull ElixirInterpolatedCharListHeredocLine o) {
    visitHeredocLine(o);
  }

  public void visitInterpolatedCharListSigilHeredoc(@NotNull ElixirInterpolatedCharListSigilHeredoc o) {
    visitCharListFragmented(o);
    // visitInterpolatedCharListHeredocLined(o);
    // visitSigilHeredoc(o);
  }

  public void visitInterpolatedCharListSigilLine(@NotNull ElixirInterpolatedCharListSigilLine o) {
    visitCharListFragmented(o);
    // visitSigilLine(o);
  }

  public void visitInterpolatedRegexBody(@NotNull ElixirInterpolatedRegexBody o) {
    visitBody(o);
  }

  public void visitInterpolatedRegexHeredoc(@NotNull ElixirInterpolatedRegexHeredoc o) {
    visitRegexFragmented(o);
    // visitSigilHeredoc(o);
  }

  public void visitInterpolatedRegexHeredocLine(@NotNull ElixirInterpolatedRegexHeredocLine o) {
    visitHeredocLine(o);
  }

  public void visitInterpolatedRegexLine(@NotNull ElixirInterpolatedRegexLine o) {
    visitRegexFragmented(o);
    // visitSigilLine(o);
  }

  public void visitInterpolatedSigilBody(@NotNull ElixirInterpolatedSigilBody o) {
    visitBody(o);
  }

  public void visitInterpolatedSigilHeredoc(@NotNull ElixirInterpolatedSigilHeredoc o) {
    visitSigilFragmented(o);
    // visitSigilHeredoc(o);
  }

  public void visitInterpolatedSigilHeredocLine(@NotNull ElixirInterpolatedSigilHeredocLine o) {
    visitHeredocLine(o);
  }

  public void visitInterpolatedSigilLine(@NotNull ElixirInterpolatedSigilLine o) {
    visitSigilFragmented(o);
    // visitSigilLine(o);
  }

  public void visitInterpolatedStringBody(@NotNull ElixirInterpolatedStringBody o) {
    visitBody(o);
  }

  public void visitInterpolatedStringHeredocLine(@NotNull ElixirInterpolatedStringHeredocLine o) {
    visitHeredocLine(o);
  }

  public void visitInterpolatedStringSigilHeredoc(@NotNull ElixirInterpolatedStringSigilHeredoc o) {
    visitStringFragmented(o);
    // visitInterpolatedStringHeredocLined(o);
    // visitSigilHeredoc(o);
  }

  public void visitInterpolatedStringSigilLine(@NotNull ElixirInterpolatedStringSigilLine o) {
    visitStringFragmented(o);
    // visitSigilLine(o);
  }

  public void visitInterpolatedWordsBody(@NotNull ElixirInterpolatedWordsBody o) {
    visitBody(o);
  }

  public void visitInterpolatedWordsHeredoc(@NotNull ElixirInterpolatedWordsHeredoc o) {
    visitWordsFragmented(o);
    // visitSigilHeredoc(o);
  }

  public void visitInterpolatedWordsHeredocLine(@NotNull ElixirInterpolatedWordsHeredocLine o) {
    visitHeredocLine(o);
  }

  public void visitInterpolatedWordsLine(@NotNull ElixirInterpolatedWordsLine o) {
    visitWordsFragmented(o);
    // visitSigilLine(o);
  }

  public void visitInterpolation(@NotNull ElixirInterpolation o) {
    visitQuotable(o);
  }

  public void visitKeywordKey(@NotNull ElixirKeywordKey o) {
    visitQuotable(o);
  }

  public void visitKeywordPair(@NotNull ElixirKeywordPair o) {
    visitQuotableKeywordPair(o);
  }

  public void visitKeywords(@NotNull ElixirKeywords o) {
    visitQuotableKeywordList(o);
  }

  public void visitList(@NotNull ElixirList o) {
    visitQuotable(o);
  }

  public void visitLiteralCharListBody(@NotNull ElixirLiteralCharListBody o) {
    visitBody(o);
  }

  public void visitLiteralCharListHeredocLine(@NotNull ElixirLiteralCharListHeredocLine o) {
    visitHeredocLine(o);
  }

  public void visitLiteralCharListSigilHeredoc(@NotNull ElixirLiteralCharListSigilHeredoc o) {
    visitCharListFragmented(o);
    // visitSigilHeredoc(o);
  }

  public void visitLiteralCharListSigilLine(@NotNull ElixirLiteralCharListSigilLine o) {
    visitCharListFragmented(o);
    // visitSigilLine(o);
  }

  public void visitLiteralRegexBody(@NotNull ElixirLiteralRegexBody o) {
    visitBody(o);
  }

  public void visitLiteralRegexHeredoc(@NotNull ElixirLiteralRegexHeredoc o) {
    visitRegexFragmented(o);
    // visitSigilHeredoc(o);
  }

  public void visitLiteralRegexHeredocLine(@NotNull ElixirLiteralRegexHeredocLine o) {
    visitHeredocLine(o);
  }

  public void visitLiteralRegexLine(@NotNull ElixirLiteralRegexLine o) {
    visitRegexFragmented(o);
    // visitSigilLine(o);
  }

  public void visitLiteralSigilBody(@NotNull ElixirLiteralSigilBody o) {
    visitBody(o);
  }

  public void visitLiteralSigilHeredoc(@NotNull ElixirLiteralSigilHeredoc o) {
    visitSigilFragmented(o);
    // visitSigilHeredoc(o);
  }

  public void visitLiteralSigilHeredocLine(@NotNull ElixirLiteralSigilHeredocLine o) {
    visitHeredocLine(o);
  }

  public void visitLiteralSigilLine(@NotNull ElixirLiteralSigilLine o) {
    visitSigilFragmented(o);
    // visitSigilLine(o);
  }

  public void visitLiteralStringBody(@NotNull ElixirLiteralStringBody o) {
    visitBody(o);
  }

  public void visitLiteralStringHeredocLine(@NotNull ElixirLiteralStringHeredocLine o) {
    visitHeredocLine(o);
  }

  public void visitLiteralStringSigilHeredoc(@NotNull ElixirLiteralStringSigilHeredoc o) {
    visitStringFragmented(o);
    // visitSigilHeredoc(o);
  }

  public void visitLiteralStringSigilLine(@NotNull ElixirLiteralStringSigilLine o) {
    visitStringFragmented(o);
    // visitSigilLine(o);
  }

  public void visitLiteralWordsBody(@NotNull ElixirLiteralWordsBody o) {
    visitBody(o);
  }

  public void visitLiteralWordsHeredoc(@NotNull ElixirLiteralWordsHeredoc o) {
    visitWordsFragmented(o);
    // visitSigilHeredoc(o);
  }

  public void visitLiteralWordsHeredocLine(@NotNull ElixirLiteralWordsHeredocLine o) {
    visitHeredocLine(o);
  }

  public void visitLiteralWordsLine(@NotNull ElixirLiteralWordsLine o) {
    visitWordsFragmented(o);
    // visitSigilLine(o);
  }

  public void visitMapArguments(@NotNull ElixirMapArguments o) {
    visitQuotable(o);
  }

  public void visitMapConstructionArguments(@NotNull ElixirMapConstructionArguments o) {
    visitQuotableArguments(o);
  }

  public void visitMapOperation(@NotNull ElixirMapOperation o) {
    visitQuotable(o);
  }

  public void visitMapPrefixOperator(@NotNull ElixirMapPrefixOperator o) {
    visitOperator(o);
  }

  public void visitMapUpdateArguments(@NotNull ElixirMapUpdateArguments o) {
    visitQuotable(o);
  }

  public void visitMatchInfixOperator(@NotNull ElixirMatchInfixOperator o) {
    visitOperator(o);
  }

  public void visitMatchedAdditionOperation(@NotNull ElixirMatchedAdditionOperation o) {
    visitMatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitMatchedAndOperation(@NotNull ElixirMatchedAndOperation o) {
    visitMatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitMatchedArrowOperation(@NotNull ElixirMatchedArrowOperation o) {
    visitMatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitMatchedAtNonNumericOperation(@NotNull ElixirMatchedAtNonNumericOperation o) {
    visitMatchedExpression(o);
    // visitModuleAttributeNameable(o);
    // visitPrefixOperation(o);
  }

  public void visitMatchedAtUnqualifiedBracketOperation(@NotNull ElixirMatchedAtUnqualifiedBracketOperation o) {
    visitMatchedExpression(o);
    // visitAtUnqualifiedBracketOperation(o);
  }

  public void visitMatchedAtUnqualifiedNoParenthesesCall(@NotNull ElixirMatchedAtUnqualifiedNoParenthesesCall o) {
    visitMatchedExpression(o);
    // visitAtUnqualifiedNoParenthesesCall(o);
    // visitMatchedCall(o);
  }

  public void visitMatchedBracketOperation(@NotNull ElixirMatchedBracketOperation o) {
    visitMatchedExpression(o);
    // visitBracketOperation(o);
  }

  public void visitMatchedCaptureNonNumericOperation(@NotNull ElixirMatchedCaptureNonNumericOperation o) {
    visitMatchedExpression(o);
    // visitPrefixOperation(o);
  }

  public void visitMatchedComparisonOperation(@NotNull ElixirMatchedComparisonOperation o) {
    visitMatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitMatchedDotCall(@NotNull ElixirMatchedDotCall o) {
    visitMatchedExpression(o);
    // visitDotCall(o);
    // visitMatchedCall(o);
  }

  public void visitMatchedExpression(@NotNull ElixirMatchedExpression o) {
    visitPsiElement(o);
  }

  public void visitMatchedInMatchOperation(@NotNull ElixirMatchedInMatchOperation o) {
    visitMatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitMatchedInOperation(@NotNull ElixirMatchedInOperation o) {
    visitMatchedExpression(o);
    // visitInOperation(o);
  }

  public void visitMatchedMatchOperation(@NotNull ElixirMatchedMatchOperation o) {
    visitMatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitMatchedMultiplicationOperation(@NotNull ElixirMatchedMultiplicationOperation o) {
    visitMatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitMatchedOrOperation(@NotNull ElixirMatchedOrOperation o) {
    visitMatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitMatchedParenthesesArguments(@NotNull ElixirMatchedParenthesesArguments o) {
    visitPsiElement(o);
  }

  public void visitMatchedPipeOperation(@NotNull ElixirMatchedPipeOperation o) {
    visitMatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitMatchedQualifiedAlias(@NotNull ElixirMatchedQualifiedAlias o) {
    visitMatchedExpression(o);
    // visitNamedElement(o);
    // visitQualifiedAlias(o);
  }

  public void visitMatchedQualifiedBracketOperation(@NotNull ElixirMatchedQualifiedBracketOperation o) {
    visitMatchedExpression(o);
    // visitQualifiedBracketOperation(o);
  }

  public void visitMatchedQualifiedNoArgumentsCall(@NotNull ElixirMatchedQualifiedNoArgumentsCall o) {
    visitMatchedExpression(o);
    // visitMatchedCall(o);
    // visitQualifiedNoArgumentsCall(o);
  }

  public void visitMatchedQualifiedNoParenthesesCall(@NotNull ElixirMatchedQualifiedNoParenthesesCall o) {
    visitMatchedExpression(o);
    // visitMatchedCall(o);
    // visitQualifiedNoParenthesesCall(o);
  }

  public void visitMatchedQualifiedParenthesesCall(@NotNull ElixirMatchedQualifiedParenthesesCall o) {
    visitMatchedExpression(o);
    // visitMatchedCall(o);
    // visitQualifiedParenthesesCall(o);
  }

  public void visitMatchedRelationalOperation(@NotNull ElixirMatchedRelationalOperation o) {
    visitMatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitMatchedTwoOperation(@NotNull ElixirMatchedTwoOperation o) {
    visitMatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitMatchedTypeOperation(@NotNull ElixirMatchedTypeOperation o) {
    visitMatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitMatchedUnaryNonNumericOperation(@NotNull ElixirMatchedUnaryNonNumericOperation o) {
    visitMatchedExpression(o);
    // visitPrefixOperation(o);
  }

  public void visitMatchedUnqualifiedBracketOperation(@NotNull ElixirMatchedUnqualifiedBracketOperation o) {
    visitMatchedExpression(o);
    // visitUnqualifiedBracketOperation(o);
  }

  public void visitMatchedUnqualifiedNoArgumentsCall(@NotNull ElixirMatchedUnqualifiedNoArgumentsCall o) {
    visitMatchedExpression(o);
    // visitMatchedCall(o);
    // visitUnqualifiedNoArgumentsCall(o);
  }

  public void visitMatchedUnqualifiedNoParenthesesCall(@NotNull ElixirMatchedUnqualifiedNoParenthesesCall o) {
    visitMatchedExpression(o);
    // visitMatchedCall(o);
    // visitUnqualifiedNoParenthesesCall(o);
  }

  public void visitMatchedUnqualifiedParenthesesCall(@NotNull ElixirMatchedUnqualifiedParenthesesCall o) {
    visitMatchedExpression(o);
    // visitMatchedCall(o);
    // visitUnqualifiedParenthesesCall(o);
  }

  public void visitMatchedWhenOperation(@NotNull ElixirMatchedWhenOperation o) {
    visitMatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitMultiplicationInfixOperator(@NotNull ElixirMultiplicationInfixOperator o) {
    visitOperator(o);
  }

  public void visitNoParenthesesArguments(@NotNull ElixirNoParenthesesArguments o) {
    visitQuotableArguments(o);
  }

  public void visitNoParenthesesExpression(@NotNull ElixirNoParenthesesExpression o) {
    visitQuotable(o);
  }

  public void visitNoParenthesesFirstPositional(@NotNull ElixirNoParenthesesFirstPositional o) {
    visitQuotable(o);
  }

  public void visitNoParenthesesKeywordPair(@NotNull ElixirNoParenthesesKeywordPair o) {
    visitQuotableKeywordPair(o);
  }

  public void visitNoParenthesesKeywords(@NotNull ElixirNoParenthesesKeywords o) {
    visitQuotableKeywordList(o);
  }

  public void visitNoParenthesesManyArguments(@NotNull ElixirNoParenthesesManyArguments o) {
    visitArguments(o);
    // visitQuotableArguments(o);
  }

  public void visitNoParenthesesManyArgumentsUnqualifiedIdentifier(@NotNull ElixirNoParenthesesManyArgumentsUnqualifiedIdentifier o) {
    visitQuotable(o);
  }

  public void visitNoParenthesesManyPositionalAndMaybeKeywordsArguments(@NotNull ElixirNoParenthesesManyPositionalAndMaybeKeywordsArguments o) {
    visitArguments(o);
    // visitQuotableArguments(o);
  }

  public void visitNoParenthesesManyStrictNoParenthesesExpression(@NotNull ElixirNoParenthesesManyStrictNoParenthesesExpression o) {
    visitPsiElement(o);
  }

  public void visitNoParenthesesOneArgument(@NotNull ElixirNoParenthesesOneArgument o) {
    visitArguments(o);
    // visitMaybeModuleName(o);
    // visitQuotableArguments(o);
  }

  public void visitNoParenthesesOnePositionalAndKeywordsArguments(@NotNull ElixirNoParenthesesOnePositionalAndKeywordsArguments o) {
    visitArguments(o);
    // visitQuotableArguments(o);
  }

  public void visitNoParenthesesStrict(@NotNull ElixirNoParenthesesStrict o) {
    visitArguments(o);
    // visitQuotableArguments(o);
  }

  public void visitOctalDigits(@NotNull ElixirOctalDigits o) {
    visitDigits(o);
  }

  public void visitOctalWholeNumber(@NotNull ElixirOctalWholeNumber o) {
    visitWholeNumber(o);
  }

  public void visitOpenHexadecimalEscapeSequence(@NotNull ElixirOpenHexadecimalEscapeSequence o) {
    visitEscapedHexadecimalDigits(o);
  }

  public void visitOrInfixOperator(@NotNull ElixirOrInfixOperator o) {
    visitOperator(o);
  }

  public void visitParenthesesArguments(@NotNull ElixirParenthesesArguments o) {
    visitArguments(o);
    // visitQuotableArguments(o);
  }

  public void visitParentheticalStab(@NotNull ElixirParentheticalStab o) {
    visitQuotable(o);
  }

  public void visitPipeInfixOperator(@NotNull ElixirPipeInfixOperator o) {
    visitOperator(o);
  }

  public void visitQuoteCharListBody(@NotNull ElixirQuoteCharListBody o) {
    visitBody(o);
  }

  public void visitQuoteHexadecimalEscapeSequence(@NotNull ElixirQuoteHexadecimalEscapeSequence o) {
    visitEscapeSequence(o);
  }

  public void visitQuoteStringBody(@NotNull ElixirQuoteStringBody o) {
    visitBody(o);
  }

  public void visitRelationalInfixOperator(@NotNull ElixirRelationalInfixOperator o) {
    visitOperator(o);
  }

  public void visitRelativeIdentifier(@NotNull ElixirRelativeIdentifier o) {
    visitQuotable(o);
  }

  public void visitSigilHexadecimalEscapeSequence(@NotNull ElixirSigilHexadecimalEscapeSequence o) {
    visitEscapeSequence(o);
  }

  public void visitSigilModifiers(@NotNull ElixirSigilModifiers o) {
    visitQuotable(o);
  }

  public void visitStab(@NotNull ElixirStab o) {
    visitQuotable(o);
  }

  public void visitStabBody(@NotNull ElixirStabBody o) {
    visitQuotable(o);
  }

  public void visitStabInfixOperator(@NotNull ElixirStabInfixOperator o) {
    visitOperator(o);
  }

  public void visitStabNoParenthesesSignature(@NotNull ElixirStabNoParenthesesSignature o) {
    visitQuotable(o);
  }

  public void visitStabOperation(@NotNull ElixirStabOperation o) {
    visitQuotable(o);
  }

  public void visitStabParenthesesSignature(@NotNull ElixirStabParenthesesSignature o) {
    visitQuotable(o);
    // visitWhenOperation(o);
  }

  public void visitStringHeredoc(@NotNull ElixirStringHeredoc o) {
    visitHeredoc(o);
    // visitInterpolatedString(o);
  }

  public void visitStringHeredocLine(@NotNull ElixirStringHeredocLine o) {
    visitHeredocLine(o);
  }

  public void visitStringLine(@NotNull ElixirStringLine o) {
    visitAtomable(o);
    // visitInterpolatedString(o);
    // visitLine(o);
    // visitQuotable(o);
  }

  public void visitStructOperation(@NotNull ElixirStructOperation o) {
    visitQuotable(o);
  }

  public void visitTuple(@NotNull ElixirTuple o) {
    visitQuotable(o);
  }

  public void visitTwoInfixOperator(@NotNull ElixirTwoInfixOperator o) {
    visitOperator(o);
  }

  public void visitTypeInfixOperator(@NotNull ElixirTypeInfixOperator o) {
    visitOperator(o);
  }

  public void visitUnaryNumericOperation(@NotNull ElixirUnaryNumericOperation o) {
    visitPrefixOperation(o);
  }

  public void visitUnaryPrefixOperator(@NotNull ElixirUnaryPrefixOperator o) {
    visitOperator(o);
  }

  public void visitUnknownBaseDigits(@NotNull ElixirUnknownBaseDigits o) {
    visitDigits(o);
  }

  public void visitUnknownBaseWholeNumber(@NotNull ElixirUnknownBaseWholeNumber o) {
    visitWholeNumber(o);
  }

  public void visitUnmatchedAdditionOperation(@NotNull ElixirUnmatchedAdditionOperation o) {
    visitUnmatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitUnmatchedAndOperation(@NotNull ElixirUnmatchedAndOperation o) {
    visitUnmatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitUnmatchedArrowOperation(@NotNull ElixirUnmatchedArrowOperation o) {
    visitUnmatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitUnmatchedAtNonNumericOperation(@NotNull ElixirUnmatchedAtNonNumericOperation o) {
    visitUnmatchedExpression(o);
    // visitPrefixOperation(o);
  }

  public void visitUnmatchedAtUnqualifiedBracketOperation(@NotNull ElixirUnmatchedAtUnqualifiedBracketOperation o) {
    visitUnmatchedExpression(o);
    // visitAtUnqualifiedBracketOperation(o);
  }

  public void visitUnmatchedAtUnqualifiedNoParenthesesCall(@NotNull ElixirUnmatchedAtUnqualifiedNoParenthesesCall o) {
    visitUnmatchedExpression(o);
    // visitAtUnqualifiedNoParenthesesCall(o);
  }

  public void visitUnmatchedBracketOperation(@NotNull ElixirUnmatchedBracketOperation o) {
    visitUnmatchedExpression(o);
    // visitBracketOperation(o);
  }

  public void visitUnmatchedCaptureNonNumericOperation(@NotNull ElixirUnmatchedCaptureNonNumericOperation o) {
    visitUnmatchedExpression(o);
    // visitPrefixOperation(o);
  }

  public void visitUnmatchedComparisonOperation(@NotNull ElixirUnmatchedComparisonOperation o) {
    visitUnmatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitUnmatchedDotCall(@NotNull ElixirUnmatchedDotCall o) {
    visitUnmatchedExpression(o);
    // visitDotCall(o);
  }

  public void visitUnmatchedExpression(@NotNull ElixirUnmatchedExpression o) {
    visitPsiElement(o);
  }

  public void visitUnmatchedInMatchOperation(@NotNull ElixirUnmatchedInMatchOperation o) {
    visitUnmatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitUnmatchedInOperation(@NotNull ElixirUnmatchedInOperation o) {
    visitUnmatchedExpression(o);
    // visitInOperation(o);
  }

  public void visitUnmatchedMatchOperation(@NotNull ElixirUnmatchedMatchOperation o) {
    visitUnmatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitUnmatchedMultiplicationOperation(@NotNull ElixirUnmatchedMultiplicationOperation o) {
    visitUnmatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitUnmatchedOrOperation(@NotNull ElixirUnmatchedOrOperation o) {
    visitUnmatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitUnmatchedPipeOperation(@NotNull ElixirUnmatchedPipeOperation o) {
    visitUnmatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitUnmatchedQualifiedAlias(@NotNull ElixirUnmatchedQualifiedAlias o) {
    visitUnmatchedExpression(o);
    // visitQualifiedAlias(o);
  }

  public void visitUnmatchedQualifiedBracketOperation(@NotNull ElixirUnmatchedQualifiedBracketOperation o) {
    visitUnmatchedExpression(o);
    // visitQualifiedBracketOperation(o);
  }

  public void visitUnmatchedQualifiedNoArgumentsCall(@NotNull ElixirUnmatchedQualifiedNoArgumentsCall o) {
    visitUnmatchedExpression(o);
    // visitQualifiedNoArgumentsCall(o);
  }

  public void visitUnmatchedQualifiedNoParenthesesCall(@NotNull ElixirUnmatchedQualifiedNoParenthesesCall o) {
    visitUnmatchedExpression(o);
    // visitQualifiedNoParenthesesCall(o);
  }

  public void visitUnmatchedQualifiedParenthesesCall(@NotNull ElixirUnmatchedQualifiedParenthesesCall o) {
    visitUnmatchedExpression(o);
    // visitQualifiedParenthesesCall(o);
  }

  public void visitUnmatchedRelationalOperation(@NotNull ElixirUnmatchedRelationalOperation o) {
    visitUnmatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitUnmatchedTwoOperation(@NotNull ElixirUnmatchedTwoOperation o) {
    visitUnmatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitUnmatchedTypeOperation(@NotNull ElixirUnmatchedTypeOperation o) {
    visitUnmatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitUnmatchedUnaryNonNumericOperation(@NotNull ElixirUnmatchedUnaryNonNumericOperation o) {
    visitUnmatchedExpression(o);
    // visitPrefixOperation(o);
  }

  public void visitUnmatchedUnqualifiedBracketOperation(@NotNull ElixirUnmatchedUnqualifiedBracketOperation o) {
    visitUnmatchedExpression(o);
    // visitUnqualifiedBracketOperation(o);
  }

  public void visitUnmatchedUnqualifiedNoArgumentsCall(@NotNull ElixirUnmatchedUnqualifiedNoArgumentsCall o) {
    visitUnmatchedExpression(o);
    // visitUnqualifiedNoArgumentsCall(o);
  }

  public void visitUnmatchedUnqualifiedNoParenthesesCall(@NotNull ElixirUnmatchedUnqualifiedNoParenthesesCall o) {
    visitUnmatchedExpression(o);
    // visitUnqualifiedNoParenthesesCall(o);
  }

  public void visitUnmatchedUnqualifiedParenthesesCall(@NotNull ElixirUnmatchedUnqualifiedParenthesesCall o) {
    visitUnmatchedExpression(o);
    // visitUnqualifiedParenthesesCall(o);
  }

  public void visitUnmatchedWhenOperation(@NotNull ElixirUnmatchedWhenOperation o) {
    visitUnmatchedExpression(o);
    // visitInfixOperation(o);
  }

  public void visitUnqualifiedNoParenthesesManyArgumentsCall(@NotNull ElixirUnqualifiedNoParenthesesManyArgumentsCall o) {
    visitPsiElement(o);
    // visitCall(o);
    // visitNoParentheses(o);
    // visitUnqualified(o);
    // visitQuotable(o);
    // visitQuotableArguments(o);
  }

  public void visitVariable(@NotNull ElixirVariable o) {
    visitQuotable(o);
  }

  public void visitWhenInfixOperator(@NotNull ElixirWhenInfixOperator o) {
    visitOperator(o);
  }

  public void visitArguments(@NotNull Arguments o) {
    visitElement(o);
  }

  public void visitAssociationOperation(@NotNull AssociationOperation o) {
    visitElement(o);
  }

  public void visitAtomable(@NotNull Atomable o) {
    visitElement(o);
  }

  public void visitBody(@NotNull Body o) {
    visitElement(o);
  }

  public void visitCharListFragmented(@NotNull CharListFragmented o) {
    visitElement(o);
  }

  public void visitDigits(@NotNull Digits o) {
    visitElement(o);
  }

  public void visitEscapeSequence(@NotNull EscapeSequence o) {
    visitElement(o);
  }

  public void visitEscapedHexadecimalDigits(@NotNull EscapedHexadecimalDigits o) {
    visitElement(o);
  }

  public void visitHeredoc(@NotNull Heredoc o) {
    visitElement(o);
  }

  public void visitHeredocLine(@NotNull HeredocLine o) {
    visitElement(o);
  }

  public void visitMaybeModuleName(@NotNull MaybeModuleName o) {
    visitElement(o);
  }

  public void visitNamedElement(@NotNull NamedElement o) {
    visitElement(o);
  }

  public void visitOperator(@NotNull Operator o) {
    visitElement(o);
  }

  public void visitPrefixOperation(@NotNull PrefixOperation o) {
    visitElement(o);
  }

  public void visitQuotable(@NotNull Quotable o) {
    visitElement(o);
  }

  public void visitQuotableArguments(@NotNull QuotableArguments o) {
    visitElement(o);
  }

  public void visitQuotableKeywordList(@NotNull QuotableKeywordList o) {
    visitElement(o);
  }

  public void visitQuotableKeywordPair(@NotNull QuotableKeywordPair o) {
    visitElement(o);
  }

  public void visitRegexFragmented(@NotNull RegexFragmented o) {
    visitElement(o);
  }

  public void visitSigilFragmented(@NotNull SigilFragmented o) {
    visitElement(o);
  }

  public void visitStringFragmented(@NotNull StringFragmented o) {
    visitElement(o);
  }

  public void visitUnquoted(@NotNull Unquoted o) {
    visitElement(o);
  }

  public void visitWholeNumber(@NotNull WholeNumber o) {
    visitElement(o);
  }

  public void visitWordsFragmented(@NotNull WordsFragmented o) {
    visitElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
