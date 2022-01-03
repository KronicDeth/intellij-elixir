package org.elixir_lang.parser_definition

class Issue2200 : ParsingTestCase() {
    fun testPipeline() {
        assertParsedAndQuotedCorrectly()
    }

    override fun getTestDataPath(): String = "${super.getTestDataPath()}/issue_2200"
}
