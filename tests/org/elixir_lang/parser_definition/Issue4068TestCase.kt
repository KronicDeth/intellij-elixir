package org.elixir_lang.parser_definition

class Issue4068TestCase : ParsingTestCase() {
    fun testStabWhenManyArguments() = assertParsedAndQuotedCorrectly()

    override fun getTestDataPath(): String = "${super.getTestDataPath()}/issue_4068"
}
