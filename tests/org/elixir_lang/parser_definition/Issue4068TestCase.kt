package org.elixir_lang.parser_definition

import org.elixir_lang.psi.quoting.QuotingDialect

class Issue4068TestCase : ParsingTestCase() {
    fun testStabWhenManyArguments() = assertParsedAndQuotedCorrectly()

    fun testAtAmbiguousDualOperator() = assertParsedAndQuotedCorrectly(false)
    fun testAtAmbiguousUnaryPlus() = assertParsedAndQuotedCorrectly(false)
    fun testAmbiguousUnaryPlusTypeOperation() = assertParsedAndQuotedCorrectly(false)
    fun testAmbiguousDualOperatorInfixArgument() = assertParsedAndQuotedCorrectly(false)
    fun testAmbiguousDualOperatorDoBlock() = assertParsedAndQuotedCorrectly(false)
    fun testAmbiguousKeywordKeyNotOperator() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_12, false)
    fun testAmbiguousKeywordKeyNewlineNotOperator() = assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_12, false)

    override fun getTestDataPath(): String = "${super.getTestDataPath()}/issue_4068"
}
