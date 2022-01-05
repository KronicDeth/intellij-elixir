package org.elixir_lang.reference.module

import org.elixir_lang.PlatformTestCase

/**
 * Checks that nested module resolution is doing substitution at alias separator ("."), instead of name.
 */
class NestedAliasVsStringTest : PlatformTestCase() {
    /*
     * Tests
     */
    fun testCompletion() {
        val completionVariants = myFixture.getCompletionVariants(
                "completion.ex",
                "nested.ex",
                "nested_suffix.ex",
                "nested_under.ex"
        )
        assertFalse(
                "Lookup contains string suffixed module name.  Nesting substitution is not breaking on '.'",
                completionVariants!!.contains("ABCDSuffix")
        )
        assertTrue(
                "Completion on `alias as:` does not complete as: aliased name",
                completionVariants.contains("ABCD")
        )
        assertTrue(
                "Completion on `alias as:` does not complete module nested under as: aliased name",
                completionVariants.contains("ABCD.Nested")
        )
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/module/nested_alias_vs_string"
}
