package org.elixir_lang.parser_definition

import org.elixir_lang.psi.quoting.QuotingDialect

class Issue2200TestCase : ParsingTestCase() {
    /**
     * `:..//` as a call argument, which reported as `<unmatched expression> expected, got ','` while
     * the operator was missing from the lexer's operator set and the atom stopped at `:..`.
     */
    fun testOperatorDefinition() {
        assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_12)
    }

    fun testPipeline() {
        assertParsedAndQuotedCorrectlyFrom(QuotingDialect.V1_12)
    }

    override fun getTestDataPath(): String = "${super.getTestDataPath()}/issue_2200"
}
