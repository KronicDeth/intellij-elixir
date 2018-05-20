package org.elixir_lang.parser_definition

class Issue1091 : ParsingTestCase() {
    fun testMapExpressionEOL() {
        assertParsedAndQuotedCorrectly()
    }

    override fun getTestDataPath(): String {
        return super.getTestDataPath() + "/issue_1091"
    }
}
