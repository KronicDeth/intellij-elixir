package org.elixir_lang.parser_definition

class Issue1316 : ParsingTestCase() {
    fun testIssue1316() {
        assertParsedAndQuotedCorrectly()
    }

    fun testList() {
        assertParsedAndQuotedCorrectly()
    }

    fun testListAddition() {
        assertParsedAndQuotedCorrectly()
    }

    override fun getTestDataPath(): String = super.getTestDataPath() + "/issue_1316"
}
