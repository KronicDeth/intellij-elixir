package org.elixir_lang.parser_definition

import org.elixir_lang.Level.V_1_6
import org.elixir_lang.test.ElixirVersion.elixirSdkLevel

@Suppress("ClassName")
class Elixir_1_6_5ParsingTestCase : ParsingTestCase() {
    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/parser_definition/elixir_1_6_5_parsing_test_case"
    }

    fun testBareDual() {
        @Suppress("LocalVariableName")
        val atLeastV_1_6 = elixirSdkLevel() >= V_1_6

        if (atLeastV_1_6) {
            assertParsedAndQuotedCorrectly(true)
        } else {
            assertTrue(!atLeastV_1_6)
        }
    }

    fun testEmptyParentheses() {
        assertParsedAndQuotedCorrectly(true)
    }
}
