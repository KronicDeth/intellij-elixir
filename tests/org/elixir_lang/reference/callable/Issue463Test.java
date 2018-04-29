package org.elixir_lang.reference.callable;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.elixir_lang.psi.ElixirIdentifier;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.CallDefinitionClause;

public class Issue463Test extends LightCodeInsightFixtureTestCase {
    /*
     * Tests
     */

    public void testDirectModuleQualifier() {
        myFixture.configureByFiles("direct_module_qualifier.ex", "referenced.ex");
        PsiElement elementAtCaret = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset());

        assertNotNull(elementAtCaret);

        PsiElement grandParent = elementAtCaret.getParent().getParent();

        assertNotNull(grandParent);
        assertInstanceOf(grandParent, Call.class);

        PsiReference reference = grandParent.getReference();

        assertNotNull(reference);

        PsiElement resolved = reference.resolve();

        assertNotNull(resolved);

        assertInstanceOf(resolved, ElixirIdentifier.class);
        assertEquals(resolved.getText(), "changeset");

        PsiElement maybeDefCall = resolved.getParent().getParent().getParent();
        assertInstanceOf(maybeDefCall, Call.class);

        assertTrue(CallDefinitionClause.Companion.is((Call) maybeDefCall));
    }

    public void testAliasedModuleQualifier() {
        myFixture.configureByFiles("aliased_module_qualifier.ex", "referenced.ex");
        PsiElement elementAtCaret = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset());

        assertNotNull(elementAtCaret);

        PsiElement grandParent = elementAtCaret.getParent().getParent();

        assertNotNull(grandParent);
        assertInstanceOf(grandParent, Call.class);

        PsiReference reference = grandParent.getReference();

        assertNotNull(reference);

        PsiElement resolved = reference.resolve();

        assertNotNull(resolved);

        assertInstanceOf(resolved, ElixirIdentifier.class);
        assertEquals(resolved.getText(), "changeset");

        PsiElement maybeDefCall = resolved.getParent().getParent().getParent();
        assertInstanceOf(maybeDefCall, Call.class);

        assertTrue(CallDefinitionClause.Companion.is((Call) maybeDefCall));
    }

    public void testDoubleAliasesModuleQualifier() {
        myFixture.configureByFiles("double_aliased_module_qualifier.ex", "referenced.ex");
        PsiElement elementAtCaret = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset());

        assertNotNull(elementAtCaret);

        PsiElement grandParent = elementAtCaret.getParent().getParent();

        assertNotNull(grandParent);
        assertInstanceOf(grandParent, Call.class);

        PsiReference reference = grandParent.getReference();

        assertNotNull(reference);

        PsiElement resolved = reference.resolve();

        assertNull(resolved);
    }

    public void testMapAccessQualifier() {
        myFixture.configureByFiles("map_access_qualifier.ex", "referenced.ex");
        PsiElement elementAtCaret = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset());

        assertNotNull(elementAtCaret);

        PsiElement grandParent = elementAtCaret.getParent().getParent();

        assertNotNull(grandParent);
        assertInstanceOf(grandParent, Call.class);

        PsiReference reference = grandParent.getReference();

        assertNotNull(reference);

        PsiElement resolved = reference.resolve();

        assertNull(resolved);
    }

    public void testUnresolvedAliasQualifier() {
        myFixture.configureByFiles("unresolved_alias_qualifier.ex", "referenced.ex");
        PsiElement elementAtCaret = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset());

        assertNotNull(elementAtCaret);

        PsiElement grandParent = elementAtCaret.getParent().getParent();

        assertNotNull(grandParent);
        assertInstanceOf(grandParent, Call.class);

        PsiReference reference = grandParent.getReference();

        assertNotNull(reference);

        PsiElement resolved = reference.resolve();

        assertNull(resolved);
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/callable/issue_463";
    }
}
