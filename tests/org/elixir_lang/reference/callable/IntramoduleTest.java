package org.elixir_lang.reference.callable;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.elixir_lang.psi.ElixirIdentifier;

public class IntramoduleTest extends LightCodeInsightFixtureTestCase {
    /*
     * Tests
     */

    public void testAmbiguousBackReference() {
        myFixture.configureByFiles("ambiguous_back.ex");
        PsiElement ambiguous = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset())
                .getParent()
                .getPrevSibling();

        assertInstanceOf(ambiguous.getFirstChild(), ElixirIdentifier.class);

        PsiReference reference = ambiguous.getReference();

        assertNotNull("`referenced` has no reference", reference);

        PsiElement resolved = reference.resolve();

        assertNotNull("`referenced` not resolved", resolved);
        assertEquals(
                "ambiguous reference does not resolve to previous function declaration",
                "def referenced do\n\n  end",
                resolved.getParent().getParent().getParent().getText()
        );
    }

    public void testAmbiguousForwardReference() {
        myFixture.configureByFiles("ambiguous_forward.ex");
        PsiElement ambiguous = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset())
                .getParent()
                .getPrevSibling();

        assertInstanceOf(ambiguous.getFirstChild(), ElixirIdentifier.class);

        PsiReference reference = ambiguous.getReference();

        assertNotNull("`referenced` has no reference", reference);

        PsiElement resolved = reference.resolve();

        assertNotNull("`referenced` not resolved", resolved);
        assertEquals(
                "ambiguous reference does not resolve to forward function declaration",
                "def referenced do\n\n  end",
                resolved.getParent().getParent().getParent().getText()
        );
    }

    public void testAmbiguousRecursiveReference() {
        myFixture.configureByFiles("ambiguous_recursive.ex");
        PsiElement ambiguous = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset())
                .getParent()
                .getPrevSibling();

        assertInstanceOf(ambiguous.getFirstChild(), ElixirIdentifier.class);

        PsiReference reference = ambiguous.getReference();

        assertNotNull("`referenced` has no reference", reference);

        PsiElement resolved = reference.resolve();

        assertNotNull("`referenced` not resolved", resolved);
        assertEquals(
                "ambiguous reference does not resolve to recursive function declaration",
                "def referenced do\n    referenced\n\n    a = 1\n  end",
                resolved.getParent().getParent().getParent().getText()
        );
    }

    public void testParenthesesRecursiveReference() {
        myFixture.configureByFiles("parentheses_recursive.ex");
        PsiElement parenthesesCall = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset())
                .getParent()
                .getParent()
                .getParent();

        assertInstanceOf(parenthesesCall.getFirstChild(), ElixirIdentifier.class);

        PsiReference reference = parenthesesCall.getReference();

        assertNotNull("`referenced` has no reference", reference);

        PsiElement resolved = reference.resolve();

        assertNotNull("`referenced` not resolved", resolved);
        assertEquals(
                "ambiguous reference does not resolve to recursive function declaration",
                "def referenced do\n    referenced()\n\n    a = 1\n  end",
                resolved.getParent().getParent().getParent().getText()
        );
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/callable/intramodule";
    }
}
