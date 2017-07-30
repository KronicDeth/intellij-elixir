package org.elixir_lang.reference.callable;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.elixir_lang.psi.ElixirIdentifier;
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall;
import org.elixir_lang.psi.call.Call;

public class Issue687Test extends LightCodeInsightFixtureTestCase {
    /*
     * Tests
     */

    public void testRepeatedMapValueInMatch() {
        myFixture.configureByFiles("repeated_map_value_in_match.ex");
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

        assertInstanceOf(resolved, UnqualifiedNoArgumentsCall.class);
        assertEquals(resolved.getText(), "nine_id");

        assertEquals(resolved.getParent().getParent().getText(), "nine_id: nine_id");
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/callable/issue_687";
    }
}
