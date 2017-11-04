package org.elixir_lang.reference.module.as;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.elixir_lang.psi.ElixirAlias;
import org.elixir_lang.psi.QualifiedAlias;

import java.util.List;

public class NestedTest extends LightPlatformCodeInsightFixtureTestCase {
    /*
     * Tests
     */

    public void testCompletion() {
        myFixture.configureByFiles("completion.ex", "suffix.ex", "nested.ex");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assertNull("Completion lookup shown", strings);
        PsiElement autoInsertedLeafElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset() - 1);
        assertNotNull(autoInsertedLeafElement);
        assertInstanceOf(autoInsertedLeafElement, LeafPsiElement.class);
        PsiElement autoInsertedCompositeElement = autoInsertedLeafElement.getParent();
        assertInstanceOf(autoInsertedCompositeElement, ElixirAlias.class);
        assertEquals(autoInsertedCompositeElement.getText(), "Nested");
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
        assertEquals(
                "alias Prefix.Suffix, as: As",
                resolveResults[0].getElement().getParent().getParent().getParent().getParent().getParent().getText()
        );
        // defmodule
        assertEquals("defmodule Prefix.Suffix.Nested do\nend", resolveResults[1].getElement().getText());
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/module/as/nested";
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        if (myFixture != null) {
            Project project = getProject();

            if (project != null && !project.isDisposed()) {
                Disposer.dispose(project);
            }
        }
    }
}
