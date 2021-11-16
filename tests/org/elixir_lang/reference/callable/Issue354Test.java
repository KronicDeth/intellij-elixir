package org.elixir_lang.reference.callable;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.elixir_lang.PlatformTestCase;
import org.elixir_lang.psi.ElixirIdentifier;
import org.elixir_lang.psi.call.Call;

public class Issue354Test extends PlatformTestCase {
    /*
     * Tests
     */

    public void testLoggerLogstashBackend() {
        myFixture.configureByFile("logger_logstash_backend.ex");
        PsiElement elementAtCaret = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        assertNotNull(elementAtCaret);
        assertInstanceOf(elementAtCaret, LeafPsiElement.class);

        PsiElement parent = elementAtCaret.getParent();

        assertNotNull(parent);
        assertInstanceOf(parent, ElixirIdentifier.class);

        PsiElement grandParent = parent.getParent();

        assertNotNull(grandParent);
        assertInstanceOf(grandParent, Call.class);

        Call grandParentCall = (Call) grandParent;
        PsiReference reference = grandParentCall.getReference();

        assertNotNull(reference);

        assertInstanceOf(reference, PsiPolyVariantReference.class);

        PsiPolyVariantReference psiPolyVariantReference = (PsiPolyVariantReference) reference;

        ResolveResult[] resolveResults = psiPolyVariantReference.multiResolve(true);

        assertEquals(resolveResults.length, 2);

        ResolveResult firstResolveResult = resolveResults[0];

        assertTrue(firstResolveResult.isValidResult());

        PsiElement firstElement = firstResolveResult.getElement();

        assertNotNull(firstElement);
        assertEquals("%{line: line, port: port} = context", firstElement.getParent().getText());

        ResolveResult secondResolveResult = resolveResults[1];

        assertTrue(secondResolveResult.isValidResult());

        PsiElement secondElement = secondResolveResult.getElement();

        assertNotNull(secondElement);
        assertEquals("context = %{backend: true}", secondElement.getParent().getText());
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/callable/issue_354";
    }
}
