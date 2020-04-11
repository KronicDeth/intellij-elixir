package org.elixir_lang.reference.module.suffix;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.elixir_lang.psi.QualifiedAlias;
import org.elixir_lang.psi.stub.index.AllName;

import java.util.Arrays;
import java.util.List;

public class NestedTest extends BasePlatformTestCase {

    /*
     * Tests
     */

    public void testCompletion() {
        myFixture.configureByFiles("completion.ex", "suffix.ex", "nested.ex");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                // nested unaliased name
                                "Prefix.Suffix.Nested",
                                // nested aliased name
                                "Suffix.Nested"
                        )
                )
        );
        assertEquals(2, strings.size());
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

        // alias
        assertEquals("alias Prefix.Suffix", resolveResults[0].getElement().getParent().getParent().getText());
        // defmodule
        assertEquals("defmodule Prefix.Suffix.Nested do\nend", resolveResults[1].getElement().getText());
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/module/suffix/nested";
    }
}
