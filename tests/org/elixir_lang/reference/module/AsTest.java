package org.elixir_lang.reference.module;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.elixir_lang.module.ElixirModuleType;
import org.elixir_lang.psi.ElixirAlias;
import org.elixir_lang.psi.stub.index.AllName;

import java.util.Arrays;
import java.util.List;

public class AsTest extends LightPlatformCodeInsightFixtureTestCase {
    /*
     * Tests
     */

    public void testCompletion() {
        myFixture.configureByFiles("completion.ex", "suffix.ex");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assertNotNull(strings);
        assertTrue(
                strings.containsAll(
                        Arrays.asList(
                                // aliased name
                                "AsAlias"
                        )
                )
        );
        assertEquals(1, strings.size());
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

        assertEquals(2, resolveResults.length);

        // alias
        assertEquals(
                "alias Prefix.Suffix, as: As",
                resolveResults[0].getElement().getParent().getParent().getParent().getParent().getParent().getText()
        );
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
        return "testData/org/elixir_lang/reference/module/as";
    }
}
