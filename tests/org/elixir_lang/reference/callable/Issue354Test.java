package org.elixir_lang.reference.callable;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.elixir_lang.psi.CallDefinitionClause;
import org.elixir_lang.psi.ElixirIdentifier;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.operation.Match;

public class Issue354Test extends BasePlatformTestCase {
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

        PsiElement resolved = reference.resolve();

        assertNotNull(resolved);
        assertInstanceOf(resolved, Call.class);

        PsiElement resolvedParent = resolved.getParent();

        assertNotNull(resolvedParent);
        assertInstanceOf(resolvedParent, Match.class);

        PsiElement resolvedGrandParent = resolvedParent.getParent();

        assertNotNull(resolvedGrandParent);

        PsiElement resolvedGreatGrandParent = resolvedGrandParent.getParent();

        assertNotNull(resolvedGreatGrandParent);

        PsiElement resolvedGreatGreatGrandParent = resolvedGreatGrandParent.getParent();

        assertNotNull(resolvedGreatGreatGrandParent);

        PsiElement resolvedGreatGreatGreatGrandParent = resolvedGreatGreatGrandParent.getParent();

        assertNotNull(resolvedGreatGreatGreatGrandParent);

        PsiElement resolvedGreatGreatGreatGreatGrandParent = resolvedGreatGreatGreatGrandParent.getParent();

        assertNotNull(resolvedGreatGreatGreatGreatGrandParent);
        assertInstanceOf(resolvedGreatGreatGreatGreatGrandParent, Call.class);

        Call resolvedGreatGreatGreatGreatGrandParentCall = (Call) resolvedGreatGreatGreatGreatGrandParent;

        assertTrue(CallDefinitionClause.is(resolvedGreatGreatGreatGreatGrandParentCall));
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/callable/issue_354";
    }
}
