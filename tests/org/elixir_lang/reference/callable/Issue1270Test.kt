package org.elixir_lang.reference.callable

import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.completeSoleCandidateAtCaret
import org.elixir_lang.code_insight.completionStringsAtCaret

/**
 * https://github.com/KronicDeth/intellij-elixir/issues/1270
 */
class Issue1270Test : PlatformTestCase() {
    /**
     * Does not complete functions from nested module when bare word is used
     */
    fun testOuterModuleBareWords() {
        myFixture.configureByFile("outer_module_bare_words.ex")

        // Bare word inside Autocomplete's own function: its public `test` and private `internal_test`
        // are both reachable via a local unqualified call, so both are offered.
        assertEquals(listOf("test", "internal_test"), myFixture.completionStringsAtCaret())
    }

    /**
     * When the relative qualifier (`State.`) is used from the outer module, its local functions are completed.
     */
    fun testOuterModuleRelativeQualifier() {
        myFixture.configureByFile("outer_module_relative_qualifier.ex")

        assertEquals(listOf("another_test"), myFixture.completionStringsAtCaret())
    }

    /**
     * The nested module cannot resolve the outer module's local functions as bare words.
     */
    fun testNestedModuleBareWords() {
        myFixture.configureByFile("nested_module_bare_words.ex")

        assertEquals(listOf("another_test"), myFixture.completionStringsAtCaret())
    }

    /**
     * The nested module can resolve the outer module using its fully-qualified name. `Autocomplete.`
     * is a **remote** call, so only the outer module's public functions are offered - the private
     * `internal_test` (`defp`) is not reachable through qualification, even from a nested module. With
     * `test` as the sole candidate for prefix `t`, it auto-inserts to `Autocomplete.test()`.
     */
    fun testNestedModuleFullQualifier() {
        myFixture.configureByFile("nested_module_full_qualifier.ex")

        // Sole candidate `test` auto-inserts (no popup); the insert handler appends `()`.
        myFixture.completeSoleCandidateAtCaret()

        myFixture.checkResultByFile("nested_module_full_qualifier_completed.ex")
    }

    /**
     * The nested module can resolve own functions when its relative name is used as a qualifier
     */
    fun testNestedModuleRelativeQualifier() {
        myFixture.configureByFile("nested_module_relative_qualifier.ex")

        assertEquals(listOf("another_test"), myFixture.completionStringsAtCaret())
    }

    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/reference/callable/issue_1270"
    }
}
