package org.elixir_lang.parser_definition

@Suppress("ClassName")
class Elixir_1_6_5ParsingTestCase : ParsingTestCase() {
    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/parser_definition/elixir_1_6_5_parsing_test_case"
    }

    fun testEmptyParentheses() {
        assertParsedAndQuotedCorrectly(true)
    }
}
