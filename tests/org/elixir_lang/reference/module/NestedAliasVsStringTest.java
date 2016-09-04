package org.elixir_lang.reference.module;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupEx;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.elixir_lang.psi.QualifiedAlias;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Checks that nested module resolution is doing substitution at alias separator ("."), instead of name.
 */
public class NestedAliasVsStringTest extends LightCodeInsightFixtureTestCase {
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
    protected void setUp() throws Exception {
        VfsRootAccess.SHOULD_PERFORM_ACCESS_CHECK = false; // TODO: a workaround for v15
        super.setUp();
    }

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/module/nested_alias_vs_string";
    }
}
