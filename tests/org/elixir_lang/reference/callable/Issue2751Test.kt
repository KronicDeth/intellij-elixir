package org.elixir_lang.reference.callable

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import org.elixir_lang.PlatformTestCase

/**
 * https://github.com/KronicDeth/intellij-elixir/issues/2751
 */
class Issue2751Test : PlatformTestCase() {
    fun testInsideIf() {
        myFixture.configureByFile("inside_if.ex")
        val completions = myFixture.complete(CompletionType.BASIC, 1)

        assertEquals(2, completions.size)

        assertInstanceOf(completions[0], LookupElementBuilder::class.java)

        val firstCompletion = completions[0] as LookupElementBuilder

        assertEquals("bar", firstCompletion.lookupString)

        assertInstanceOf(completions[1], LookupElementBuilder::class.java)

        val secondCompletion = completions[1] as LookupElementBuilder

        assertEquals("baz", secondCompletion.lookupString)
    }

    fun testOutsideIf() {
        myFixture.configureByFile("outside_if.ex")
        val completions = myFixture.complete(CompletionType.BASIC, 1)

        assertEquals(2, completions.size)

        assertInstanceOf(completions[0], LookupElementBuilder::class.java)

        val firstCompletion = completions[0] as LookupElementBuilder

        assertEquals("bar", firstCompletion.lookupString)

        assertInstanceOf(completions[1], LookupElementBuilder::class.java)

        val secondCompletion = completions[1] as LookupElementBuilder

        assertEquals("baz", secondCompletion.lookupString)
    }

    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/reference/callable/issue_2751"
    }
}
