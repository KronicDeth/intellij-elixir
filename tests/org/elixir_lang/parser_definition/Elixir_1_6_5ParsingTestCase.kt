package org.elixir_lang.parser_definition

class Elixir_1_6_5ParsingTestCase : ParsingTestCase() {
    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/parser_definition/elixir_1_6_5_parsing_test_case"
    }

    fun testBareDual() {
        assertParsedAndQuotedCorrectly(true)
    }
}
