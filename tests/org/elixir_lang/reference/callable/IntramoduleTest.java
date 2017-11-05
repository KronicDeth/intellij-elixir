package org.elixir_lang.reference.callable;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.*;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.elixir_lang.psi.ElixirIdentifier;

import static org.junit.Assert.assertNotEquals;

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

    public void testFunctionNameMultipleSameArity() {
        myFixture.configureByFiles("function_name_multiple_same_arity.ex");
        PsiElement parenthesesCall = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset())
                .getParent()
                .getParent()
                .getParent();

        assertInstanceOf(parenthesesCall.getFirstChild(), ElixirIdentifier.class);

        PsiReference reference = parenthesesCall.getReference();

        assertNotNull("`referenced` has no reference", reference);

        assertInstanceOf(reference, PsiPolyVariantReference.class);

        PsiPolyVariantReference polyVariantReference = (PsiPolyVariantReference) reference;

        ResolveResult[] resolveResults = polyVariantReference.multiResolve(false);

        assertNotEquals("Resolved to both clauses instead of selected clause", 2, resolveResults.length);
        assertEquals("Resolves to self", 1, resolveResults.length);

        PsiElement resolved = reference.resolve();

        assertNotNull("Reference not resolved", resolved);
        assertEquals("def referenced(true) do\n  end", resolved.getParent().getParent().getParent().getText());
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

    public void testParenthesesMultipleCorrectArityReference() {
        myFixture.configureByFiles("parentheses_multiple_correct_arity.ex");
        PsiElement parenthesesCall = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset())
                .getParent()
                .getParent()
                .getParent();

        assertInstanceOf(parenthesesCall.getFirstChild(), ElixirIdentifier.class);

        PsiReference reference = parenthesesCall.getReference();

        assertNotNull("`referenced` has no reference", reference);

        assertInstanceOf(reference, PsiPolyVariantReference.class);

        PsiPolyVariantReference polyVariantReference = (PsiPolyVariantReference) reference;

        ResolveResult[] resolveResults = polyVariantReference.multiResolve(false);

        assertEquals("Did not resolve to both clauses", 2, resolveResults.length);

        ResolveResult firstResolveResult = resolveResults[0];
        PsiElement firstResolved = firstResolveResult.getElement();

        assertEquals(
                "first ResolveResult is not the true clause",
                "def referenced(true) do\n  end",
                firstResolved.getParent().getParent().getParent().getText()
        );

        assertInstanceOf(firstResolved, NavigatablePsiElement.class);

        NavigatablePsiElement navigatableFirstResolved = (NavigatablePsiElement) firstResolved;

        ItemPresentation firstPresentation = navigatableFirstResolved.getPresentation();

        assertNotNull("first ResolveResult element has no presentation", firstPresentation);

        assertEquals("A", firstPresentation.getLocationString());
        assertEquals("referenced(true)", firstPresentation.getPresentableText());

        ResolveResult secondResolveResult = resolveResults[1];
        PsiElement secondResolved = secondResolveResult.getElement();

        assertEquals(
                "second ResolveResult is not the false clause",
                "def referenced(false) do\n  end",
                secondResolved.getParent().getParent().getParent().getText()
        );

        assertInstanceOf(secondResolved, NavigatablePsiElement.class);

        NavigatablePsiElement navigatableSecondResolved = (NavigatablePsiElement) secondResolved;

        ItemPresentation secondPresentation = navigatableSecondResolved.getPresentation();

        assertNotNull("second ResolveResult element has no presentation", secondPresentation);

        assertEquals("A", secondPresentation.getLocationString());
        assertEquals("referenced(false)", secondPresentation.getPresentableText());
    }

    public void testParenthesesSingleCorrectArityReference() {
        myFixture.configureByFiles("parentheses_single_correct_arity.ex");
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
                "parentheses 1-arity reference does not resolve to single 1-arity function declaration",
                "def referenced(_) do\n  end",
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
