package org.elixir_lang.reference.module.suffix;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import org.elixir_lang.PlatformTestCase;
import org.elixir_lang.psi.QualifiedAlias;

import java.util.List;

public class NestedTest extends PlatformTestCase {

    /*
     * Tests
     */

    public void testCompletion() {
        myFixture.configureByFiles("completion.ex", "suffix.ex", "nested.ex");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertNotNull(strings);
        assertEquals(1, strings.size());
        assertEquals("Nested", strings.get(0));
    }

    public void testReference() {
        myFixture.configureByFiles("reference.ex", "suffix.ex", "nested.ex");
        PsiElement alias = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset())
                .getParent()
                .getPrevSibling();

        assertInstanceOf(alias, QualifiedAlias.class);
        PsiPolyVariantReference polyVariantReference = (PsiPolyVariantReference) alias.getReference();

        assertNotNull(polyVariantReference);

        ResolveResult[] resolveResults = polyVariantReference.multiResolve(false);

        assertEquals(2, resolveResults.length);

        // defmodule
        assertEquals("defmodule Prefix.Suffix.Nested do\nend", resolveResults[0].getElement().getText());
        // alias
        assertEquals("alias Prefix.Suffix", resolveResults[1].getElement().getText());
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/module/suffix/nested";
    }
}
