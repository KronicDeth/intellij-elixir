package org.elixir_lang.psi.scope.call_definition_clause

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import org.elixir_lang.PlatformTestCase

class VariantsTest : PlatformTestCase() {
    fun testIssue453() {
        myFixture.configureByFiles("defmodule.ex")
        myFixture.complete(CompletionType.BASIC)
        val strings = myFixture.lookupElementStrings
        assertNotNull("Completion not shown", strings)
        assertEquals("Wrong number of completions", 0, strings!!.size)
    }

    fun testIssue462() {
        myFixture.configureByFiles("self_completion.ex")
        myFixture.complete(CompletionType.BASIC)
        val variants = myFixture.lookupElementStrings.orEmpty()
        assertFalse(
            "Function currently being defined should not appear in completion variants",
            variants.contains("the_function_currently_being_defined")
        )
    }

    fun testIssue2073() {
        myFixture.configureByFile("callback.ex")
        val variants = myFixture.complete(CompletionType.BASIC).orEmpty().toList()

        assertRenderedVariant(
            variants,
            lookupString = "function_callback",
            expectedTailText = "/0 (callback.ex defmodule Behaviour)"
        )
        assertRenderedVariant(
            variants,
            lookupString = "macro_callback",
            expectedTailText = "/1 (callback.ex defmodule Behaviour)"
        )
    }

    /**
     * Finds the completion [LookupElement] whose lookup string is [lookupString], renders it, and
     * asserts its item text equals [lookupString] and its tail text equals [expectedTailText].
     */
    private fun assertRenderedVariant(
        variants: List<LookupElement>,
        lookupString: String,
        expectedTailText: String
    ) {
        val variant = variants.find { it.lookupString == lookupString }
        assertNotNull("Completion variant '$lookupString' not offered", variant)

        val presentation = LookupElementPresentation()
        variant!!.renderElement(presentation)
        assertEquals(lookupString, presentation.itemText)
        assertEquals(expectedTailText, presentation.tailText)
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/psi/scope/call_definition_clause/variants"
}
