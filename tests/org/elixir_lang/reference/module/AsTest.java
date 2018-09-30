package org.elixir_lang.reference.module;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.elixir_lang.psi.ElixirAlias;

import java.util.List;

public class AsTest extends LightPlatformCodeInsightFixtureTestCase {
    /*
     * Tests
     */

    public void testCompletion() {
        List<String> completionVariants = myFixture.getCompletionVariants(
                "completion.ex",
                "suffix1.ex",
                "suffix2.ex"
        );
        assertTrue(
                "AsA was not completed to AsAlias1",
                completionVariants.contains("AsAlias1")
        );
        assertTrue(
                "AsA was not completed to AsAlias2",
                completionVariants.contains("AsAlias2")
        );
        assertEquals(2, completionVariants.size());
    }

    public void testReference() {
        myFixture.configureByFiles("reference.ex", "suffix1.ex");
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

        assertEquals(3, resolveResults.length);

        // alias .., as:
        assertEquals(
                "alias Prefix.Suffix1, as: As",
                resolveResults[0].getElement().getParent().getParent().getParent().getParent().getParent().getText()
        );
        // alias ..
        assertEquals(
                "alias Prefix.Suffix1, as: As",
                resolveResults[1].getElement().getParent().getParent().getText()
        );
        // defmodule ..
        assertEquals("defmodule Prefix.Suffix1 do\nend", resolveResults[2].getElement().getText());
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/module/as";
    }
}
