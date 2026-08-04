package org.elixir_lang.parser_definition

class Issue1105TestCase : ParsingTestCase() {
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
