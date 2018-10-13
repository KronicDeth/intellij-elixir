package org.elixir_lang.reference.callable

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import junit.framework.TestCase

/**
 * https://github.com/KronicDeth/intellij-elixir/issues/1270
 */
class Issue1270Test : LightCodeInsightFixtureTestCase() {
    /**
     * Does not complete functions from nested module when bare word is used
     */
    fun testOuterModuleBareWords() {
        myFixture.configureByFile("outer_module_bare_words.ex")
        val completions = myFixture.complete(CompletionType.BASIC, 1)

        assertEquals(2, completions.size)

        assertInstanceOf(completions[0], LookupElementBuilder::class.java)

        val firstCompletion = completions[0] as LookupElementBuilder

        assertEquals("test", firstCompletion.lookupString)

        assertInstanceOf(completions[1], LookupElementBuilder::class.java)

        val secondCompletion = completions[1] as LookupElementBuilder

        assertEquals("internal_test", secondCompletion.lookupString)
    }

    /**
     * When the relative qualifier (`State.`) is used from the outer module, its local functions are completed.
     */
    fun testOuterModuleRelativeQualifier() {
        myFixture.configureByFile("outer_module_relative_qualifier.ex")
        val completions = myFixture.complete(CompletionType.BASIC, 1)

        assertEquals(1, completions.size)

        assertInstanceOf(completions[0], LookupElementBuilder::class.java)

        val firstCompletion = completions[0] as LookupElementBuilder

        assertEquals("another_test", firstCompletion.lookupString)
    }

    /**
     * The nested module cannot resolve the outer module's local functions as bare words.
     */
    fun testNestedModuleBareWords() {
        myFixture.configureByFile("nested_module_bare_words.ex")
        val completions = myFixture.complete(CompletionType.BASIC, 1)

        assertEquals(1, completions.size)

        assertInstanceOf(completions[0], LookupElementBuilder::class.java)

        val firstCompletion = completions[0] as LookupElementBuilder

        assertEquals("another_test", firstCompletion.lookupString)
    }

    /**
     * The nested module can resolve outer module using fully-qualified name
     */
    fun testNestedModuleFullQualifier() {
        myFixture.configureByFile("nested_module_full_qualifier.ex")
        val completions = myFixture.complete(CompletionType.BASIC, 1)

        assertEquals(2, completions.size)

        assertInstanceOf(completions[0], LookupElementBuilder::class.java)

        val firstCompletion = completions[0] as LookupElementBuilder

        assertEquals("test", firstCompletion.lookupString)

        assertInstanceOf(completions[1], LookupElementBuilder::class.java)

        val secondCompletion = completions[1] as LookupElementBuilder

        assertEquals("internal_test", secondCompletion.lookupString)
    }

    /**
     * The nested module can resolve own functions when its relative name is used as a qualifier
     */
    fun testNestedModuleRelativeQualifier() {
        myFixture.configureByFile("nested_module_relative_qualifier.ex")
        val completions = myFixture.complete(CompletionType.BASIC, 1)

        assertEquals(1, completions.size)

        assertInstanceOf(completions[0], LookupElementBuilder::class.java)

        val firstCompletion = completions[0] as LookupElementBuilder

        assertEquals("another_test", firstCompletion.lookupString)
    }

    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/reference/callable/issue_1270"
    }
}
