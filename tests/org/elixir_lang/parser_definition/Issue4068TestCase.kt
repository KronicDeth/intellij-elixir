package org.elixir_lang.parser_definition

import org.elixir_lang.psi.quoting.QuotingDialect

class Issue4068TestCase : ParsingTestCase() {
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

    fun testEmptyInterpolation() = assertParsedAndQuotedCorrectly(false)
    fun testEmptyInterpolationNewline() = assertParsedAndQuotedCorrectly(false)
    fun testEmptyInterpolationLaterLine() = assertParsedAndQuotedCorrectly(false)

    fun testSemicolon() = assertParsedAndQuotedCorrectly(false)

    fun testCharacterNewlineLine() = assertParsedAndQuotedCorrectly(false)
    fun testCharacterEscapedNewlineLine() = assertParsedAndQuotedCorrectly(false)

    override fun getTestDataPath(): String = "${super.getTestDataPath()}/issue_4068"
}
