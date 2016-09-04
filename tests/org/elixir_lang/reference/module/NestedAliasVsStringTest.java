package org.elixir_lang.reference.module;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.elixir_lang.psi.QualifiedAlias;

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
        myFixture.configureByFiles("completion.ex", "nested.ex", "nested_suffix.ex");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assertFalse(
                "Lookup contains string suffixed module name.  Nesting substitution is not breaking on '.'",
                strings.contains("Prefix.Suffix.NestedSuffix")
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
