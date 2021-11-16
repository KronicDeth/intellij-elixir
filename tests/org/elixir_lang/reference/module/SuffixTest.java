package org.elixir_lang.reference.module;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import org.elixir_lang.PlatformTestCase;
import org.elixir_lang.psi.ElixirAlias;

import java.util.Arrays;
import java.util.List;

public class SuffixTest extends PlatformTestCase {

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

        // defmodule
        assertEquals("defmodule Prefix.Suffix do\nend", resolveResults[0].getElement().getText());
        // alias
        assertEquals("alias Prefix.Suffix", resolveResults[1].getElement().getText());
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/module/suffix";
    }
}
