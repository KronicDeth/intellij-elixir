package org.elixir_lang.reference.module;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.elixir_lang.psi.ElixirAlias;

import java.util.Arrays;
import java.util.List;

public class SuffixTest extends LightCodeInsightFixtureTestCase {

    /*
     * Tests
     */

    public void testCompletion() {
        myFixture.configureByFiles("completion.ex", "suffix.ex");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                // unaliased name
                                "Prefix.Suffix",
                                // aliased name
                                "Suffix"
                        )
                )
        );
        assertEquals(2, strings.size());
    }

    public void testReference() {
        myFixture.configureByFiles("reference.ex", "suffix.ex");
        PsiElement alias = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset())
                .getParent()
                .getPrevSibling()
                .getFirstChild();

        assertInstanceOf(alias, ElixirAlias.class);
        PsiPolyVariantReference polyVariantReference = (PsiPolyVariantReference) alias.getReference();

        assertNotNull(polyVariantReference);

        ResolveResult[] resolveResults = polyVariantReference.multiResolve(false);

        assertEquals(resolveResults.length, 2);

        // alias
        assertEquals("alias Prefix.Suffix", resolveResults[0].getElement().getParent().getParent().getText());
        // defmodule
        assertEquals("defmodule Prefix.Suffix do\nend", resolveResults[1].getElement().getText());
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
        return "testData/org/elixir_lang/reference/module/suffix";
    }
}
