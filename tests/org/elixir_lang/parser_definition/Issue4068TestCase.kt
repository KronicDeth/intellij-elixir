package org.elixir_lang.parser_definition

import org.elixir_lang.psi.quoting.QuotingDialect

class Issue4068TestCase : ParsingTestCase() {
    fun testNullaryRangeParenthesized() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_14, false)
    fun testNullaryRangeArgument() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_14, false)
    fun testNullaryRangeOperatorDefinition() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_14, false)
    fun testNullaryRangeNot() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_14, false)
    fun testNullaryRangeMatchOperands() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_14, false)
    fun testNullaryRangeContainers() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_14, false)
    fun testNullaryRangeNoParenthesesArguments() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_14, false)
    fun testNullaryRangeEndOfLine() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_14, false)
    fun testRangeOperatorNewline() = assertParsedAndQuotedCorrectly(false)
    fun testRangeSpacedOperands() = assertParsedAndQuotedCorrectly(false)

    fun testNoParenthesesManyArgumentsDoBlockArgument() = assertParsedAndQuotedCorrectly(false)
    fun testForManyArgumentsDoBlockArgument() = assertParsedAndQuotedCorrectly(false)
    fun testForManyArgumentsDoBlockSecondArgument() = assertParsedAndQuotedCorrectly(false)
    fun testWithManyArgumentsDoBlockArgument() = assertParsedAndQuotedCorrectly(false)

    fun testMultiLetterSigilDoubleQuotes() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_15, false)
    fun testMultiLetterSigilBracketsModifier() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_15, false)
    fun testMultiLetterSigilParenthesesModifiers() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_15, false)
    fun testMultiLetterSigilSingleQuotes() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_15, false)
    fun testMultiLetterSigilHeredoc() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_15, false)
    fun testMultiLetterSigilDigits() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_17, false)
    fun testMultiLetterSigilUnknown() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_15, false)
    fun testMultiLetterSigilEmpty() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_15, false)
    fun testMultiLetterSigilBraces() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_15, false)

    fun testPinnedStructName() = assertParsedAndQuotedCorrectly(false)
    fun testPinnedStructNameEmpty() = assertParsedAndQuotedCorrectly(false)
    fun testPrefixedStructNames() = assertParsedAndQuotedCorrectly(false)
    fun testStepStructName() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_13, false)
    fun testEllipsisStructName() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_17, false)
    fun testPinnedStructNameAccess() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_17, false)

    fun testUnicodeRemoteCall() = assertParsedAndQuotedCorrectly(false)
    fun testCombiningMarkIdentifierForms() = assertParsedAndQuotedCorrectly(false)
    fun testDecomposedIdentifier() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_14, false)
    fun testDecomposedIdentifierForms() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_14, false)

    fun testStabWhenManyArguments() = assertParsedAndQuotedCorrectly()

    fun testHexadecimalByteEscapeString() = assertParsedAndQuotedCorrectly(false)
    fun testHexadecimalByteEscapeAfterMultibyte() = assertParsedAndQuotedCorrectly(false)
    fun testHexadecimalByteEscapeHeredoc() = assertParsedAndQuotedCorrectly(false)
    fun testHexadecimalByteEscapeCharList() =
        assertParsedAndQuotedAroundErrorOrRaise(QuotingDialect.V1_19, "Elixir.UnicodeConversionError", false)
    fun testHexadecimalByteEscapeUtf8String() = assertParsedAndQuotedCorrectly(false)
    fun testHexadecimalByteEscapeUtf8CharList() = assertParsedAndQuotedCorrectly(false)
    fun testHexadecimalByteEscapeInterpolated() = assertParsedAndQuotedCorrectly(false)

    fun testAtAmbiguousDualOperator() = assertParsedAndQuotedCorrectly(false)
    fun testAtAmbiguousUnaryPlus() = assertParsedAndQuotedCorrectly(false)
    fun testAmbiguousUnaryPlusTypeOperation() = assertParsedAndQuotedCorrectly(false)
    fun testAmbiguousDualOperatorInfixArgument() = assertParsedAndQuotedCorrectly(false)
    fun testAmbiguousDualOperatorDoBlock() = assertParsedAndQuotedCorrectly(false)
    fun testAmbiguousKeywordKeyNotOperator() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_12, false)
    fun testAmbiguousKeywordKeyNewlineNotOperator() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_12, false)

    fun testLiteralSigilEscapedNewline() = assertParsedAndQuotedCorrectly(false)

    fun testHeredocSpacesThenTabs() = assertParsedAndQuotedCorrectly(false)
    fun testCharListHeredocSpacesThenTabs() = assertParsedAndQuotedCorrectly(false)

    fun testSteppedRangeVariables() = assertParsedAndQuotedCorrectly(false)
    fun testSteppedRangeCallForms() = assertParsedAndQuotedCorrectly(false)

    fun testCaptureStepOperator() = assertParsedAndQuotedCorrectly(false)
    fun testCaptureStepOperatorEscapedNewline() = assertParsedAndQuotedCorrectly(false)
    fun testStepOperatorUnary() = assertParsedAndQuotedCorrectly(false)
    fun testCaptureOperatorEscapedNewline() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_20, false)
    fun testCaptureOperatorsEscapedNewline() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_20, false)

    fun testDotKeywordKey() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_13, false)
    fun testSteppedRangeKeywordKey() = assertParsedAndQuotedCorrectly(false)
    fun testSteppedRangeKeywordKeyForms() = assertParsedAndQuotedCorrectly(false)

    fun testMapNonPairEntries() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_17, false)
    fun testMapTupleEntry() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_17, false)
    fun testMapNonPairEntryForms() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_17, false)

    fun testNotInLineStart() = assertParsedAndQuotedCorrectly(false)
    fun testNotInLineStartNewline() = assertParsedAndQuotedCorrectly(false)
    fun testNotLineStartInPrefixedIdentifier() = assertParsedAndQuotedCorrectly(false)

    fun testRemoteCallAfterDotNewline() = assertParsedAndQuotedCorrectly(false)
    fun testRemoteParenthesesCallAfterDotNewline() = assertParsedAndQuotedCorrectly(false)
    fun testRemoteParenthesesCallArgumentsAfterDotNewline() = assertParsedAndQuotedCorrectly(false)
    fun testRemoteNoParenthesesCallAfterDotNewline() = assertParsedAndQuotedCorrectly(false)

    fun testMicroSignIdentifier() = assertParsedAndQuotedCorrectly(false)
    fun testMicroSignMatch() = assertParsedAndQuotedCorrectly(false)
    fun testMicroSignIdentifierForms() = assertParsedAndQuotedCorrectly(false)

    fun testEmptyInterpolation() = assertParsedAndQuotedCorrectly(false)
    fun testEmptyInterpolationNewline() = assertParsedAndQuotedCorrectly(false)
    fun testEmptyInterpolationLaterLine() = assertParsedAndQuotedCorrectly(false)

    fun testEscapedNewlineBeforeDualOperator() = assertParsedAndQuotedCorrectly(false)
    fun testEscapedNewlineAfterDualOperator() = assertParsedAndQuotedCorrectly(false)
    fun testEscapedNewlineAfterDualOperatorForms() = assertParsedAndQuotedCorrectly(false)
    fun testEscapedNewlineBeforeDualOperatorOperand() = assertParsedAndQuotedCorrectly(false)
    fun testEscapedNewlineBeforeDualOperatorOperandForms() = assertParsedAndQuotedCorrectly(false)
    fun testUnspacedEscapedNewlineBeforeDualOperatorOperand() = assertParsedAndQuotedCorrectly(false)

    fun testCaptureEllipsis() = assertParsedAndQuotedCorrectly(false)
    fun testEllipsisDivision() = assertParsedAndQuotedCorrectly(false)

    fun testParenthesizedRangeStep() = assertParsedAndQuotedCorrectlyFromOrParsedWithErrors(QuotingDialect.V1_12, false)

    fun testCharacterOutsideBasicMultilingualPlane() = assertParsedAndQuotedCorrectly(false)

    fun testQuotedRemoteCallNameEscape() = assertParsedAndQuotedCorrectly(false)
    fun testCaptureQuotedRemoteCallNameEscape() = assertParsedAndQuotedCorrectly(false)
    fun testCaptureParenthesesQuotedRemoteCallNameEscape() = assertParsedAndQuotedCorrectly(false)
    fun testQuotedRemoteCallNameInvalidEscape() =
        assertParsedAndQuotedCorrectlyBeforeOrRaise(QuotingDialect.V1_18, QuotingDialect.V1_19, "Elixir.MatchError", false)
    fun testQuotedRemoteCallNameInvalidUnicodeEscape() =
        assertParsedAndQuotedCorrectlyBeforeOrRaise(QuotingDialect.V1_18, QuotingDialect.V1_19, "Elixir.MatchError", false)
    fun testQuotedRemoteCallNameInvalidBracedEscape() =
        assertParsedAndQuotedCorrectlyBeforeOrRaise(QuotingDialect.V1_18, QuotingDialect.V1_19, "Elixir.MatchError", false)
    fun testInvalidHexadecimalEscapeString() = assertParsedWithErrors(false)
    fun testQuotedRemoteCallNameInvalidCodePoint() =
        assertParsedAndQuotedCorrectlyBeforeOrRaise(QuotingDialect.V1_18, QuotingDialect.V1_19, "Elixir.MatchError", false)
    fun testQuotedRemoteCallNameSurrogate() =
        assertParsedAndQuotedCorrectlyBeforeOrRaise(QuotingDialect.V1_18, QuotingDialect.V1_19, "Elixir.MatchError", false)

    fun testEscapedLineSeparator() = assertParsedAndQuotedCorrectlyBefore(QuotingDialect.V1_20, false)
    fun testEscapedLineSeparatorLiteralSigil() = assertParsedAndQuotedCorrectlyBefore(QuotingDialect.V1_20, false)
    fun testLineSeparatorHeredoc() = assertParsedAndQuotedCorrectlyBefore(QuotingDialect.V1_20, false)

    fun testSemicolon() = assertParsedAndQuotedCorrectly(false)

    fun testCharacterNewlineLine() = assertParsedAndQuotedCorrectly(false)
    fun testCharacterEscapedNewlineLine() = assertParsedAndQuotedCorrectly(false)

    override fun getTestDataPath(): String = "${super.getTestDataPath()}/issue_4068"
}
