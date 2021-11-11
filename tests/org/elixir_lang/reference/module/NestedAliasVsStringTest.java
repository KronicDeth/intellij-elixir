package org.elixir_lang.reference.module;

import org.elixir_lang.PlatformTestCase;

import java.util.List;

/**
 * Checks that nested module resolution is doing substitution at alias separator ("."), instead of name.
 */
public class NestedAliasVsStringTest extends PlatformTestCase {
    /*
     * Tests
     */

    public void testCompletion() {
        List<String> completionVariants = myFixture.getCompletionVariants(
                "completion.ex",
                "nested.ex",
                "nested_suffix.ex",
                "nested_under.ex"
        );
        assertFalse(
                "Lookup contains string suffixed module name.  Nesting substitution is not breaking on '.'",
                completionVariants.contains("ABCDSuffix")
        );
        assertTrue(
                "Completion on `alias as:` does not complete as: aliased name",
                completionVariants.contains("ABCD")
        );
        assertTrue(
                "Completion on `alias as:` does not complete module nested under as: aliased name",
                completionVariants.contains("ABCD.Nested")
        );
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/module/nested_alias_vs_string";
    }
}
