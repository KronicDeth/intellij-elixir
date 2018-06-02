package org.elixir_lang.parser_definition

class Issue1105 : ParsingTestCase() {
    fun testAssociatedAtom() {
        assertParsedAndQuotedCorrectly()
    }

    fun testAssociatedBoolean() {
        assertParsedAndQuotedCorrectly()
    }

    fun testKeywordBoolean() {
        assertParsedAndQuotedCorrectly()
    }

    override fun getTestDataPath(): String {
        return super.getTestDataPath() + "/issue_1105"
    }
}
