package org.elixir_lang.parser_definition

class Issue2200TestCase : ParsingTestCase() {
    /**
     * `:..//` as a call argument, which reported as `<unmatched expression> expected, got ','` while
     * the operator was missing from the lexer's operator set and the atom stopped at `:..`.
     */
    fun testOperatorDefinition() {
        assertParsedAndQuotedCorrectly()
    }

    fun testPipeline() {
        assertParsedAndQuotedCorrectly()
    }

    override fun getTestDataPath(): String = "${super.getTestDataPath()}/issue_2200"
}
