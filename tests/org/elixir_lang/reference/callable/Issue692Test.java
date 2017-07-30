package org.elixir_lang.reference.callable;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.elixir_lang.psi.ElixirIdentifier;
import org.elixir_lang.psi.call.Call;

public class Issue692Test extends LightCodeInsightFixtureTestCase {
    /*
     * Tests
     */

    public void testUnresolvedAtTopOfFile() {
        myFixture.configureByFiles("unresolved_at_top_of_file.ex");
        PsiElement elementAtCaret = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset());

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
        assertTrue(resolved.isEquivalentTo(grandParent));
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/callable/issue_692";
    }
}
