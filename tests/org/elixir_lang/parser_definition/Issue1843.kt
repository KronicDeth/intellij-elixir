package org.elixir_lang.parser_definition

class Issue1843 : ParsingTestCase() {
    fun testCharListHeredoc() {
        assertParsedAndQuotedCorrectly()
    }

    fun testLiteralCharListSigilHeredoc() {
        assertParsedAndQuotedCorrectly()
    }

    fun testLiteralRegexHeredoc() {
        assertParsedAndQuotedCorrectly()
    }

    fun testLiteralSigilHeredoc() {
        assertParsedAndQuotedCorrectly()
    }

    fun testLiteralStringHeredoc() {
        assertParsedAndQuotedCorrectly()
    }

    fun testLiteralWordsHeredoc() {
        assertParsedAndQuotedCorrectly()
    }

    fun testInterpolatedCharListSigilHeredoc() {
        assertParsedAndQuotedCorrectly()
    }

    fun testInterpolatedRegexHeredoc() {
        assertParsedAndQuotedCorrectly()
    }

    fun testInterpolatedSigilHeredoc() {
        assertParsedAndQuotedCorrectly()
    }

    fun testInterpolatedStringHeredoc() {
        assertParsedAndQuotedCorrectly()
    }

    fun testInterpolatedWordsHeredoc() {
        assertParsedAndQuotedCorrectly()
    }

    fun testStringHeredoc() {
        assertParsedAndQuotedCorrectly()
    }

    override fun getTestDataPath(): String = super.getTestDataPath() + "/issue_1843"
}
