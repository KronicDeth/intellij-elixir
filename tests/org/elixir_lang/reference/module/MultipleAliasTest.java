package org.elixir_lang.reference.module;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.elixir_lang.psi.ElixirAlias;
import org.elixir_lang.psi.stub.index.AllName;

import java.util.Arrays;
import java.util.List;

public class MultipleAliasTest extends LightCodeInsightFixtureTestCase {

    /*
     * Tests
     */

    public void testCompletion() {
        myFixture.configureByFiles("completion.ex", "multiple_alias_aye.ex", "multiple_alias_bee.ex");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                // unaliased names
                                "Prefix.MultipleAliasAye",
                                // aliased name
                                "MultipleAliasAye"
                        )
                )
        );
        assertEquals(2, strings.size());
    }

    public void testCompletionInSideCurlies() {
        myFixture.configureByFiles("completion_inside_curlies.ex", "multiple_alias_aye.ex", "multiple_alias_bee.ex");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                "MultipleAliasAye",
                                "MultipleAliasBee"
                        )
                )
        );
        assertEquals(2, strings.size());
    }

    public void testReference() {
        myFixture.configureByFiles("reference.ex", "multiple_alias_aye.ex", "multiple_alias_bee.ex");
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

        assertEquals(2, resolveResults.length);

        // alias
        assertEquals(
                "alias Prefix.{MultipleAliasAye, MultipleAliasBee}",
                resolveResults[0].getElement().getParent().getParent().getParent().getParent().getParent().getText()
        );
        // defmodule
        assertEquals("defmodule Prefix.MultipleAliasAye do\nend", resolveResults[1].getElement().getText());
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/module/multiple_alias";
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
