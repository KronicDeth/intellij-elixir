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
                .getPrevSibling();

        assertInstanceOf(ambiguous.getFirstChild(), ElixirIdentifier.class);

        PsiReference reference = ambiguous.getReference();

        assertNotNull("`referenced` has no reference", reference);

        PsiElement resolved = reference.resolve();

        assertNotNull("`referenced` not resolved", resolved);
        assertEquals(
                "ambiguous reference does not resolve to previous function declaration",
                "def referenced do\n\n  end",
                resolved.getText()
        );
    }

    public void testAmbiguousForwardReference() {
        myFixture.configureByFiles("ambiguous_forward.ex");
        PsiElement ambiguous = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset())
                .getPrevSibling();

        assertInstanceOf(ambiguous.getFirstChild(), ElixirIdentifier.class);

        PsiReference reference = ambiguous.getReference();

        assertNotNull("`referenced` has no reference", reference);

        PsiElement resolved = reference.resolve();

        assertNotNull("`referenced` not resolved", resolved);
        assertEquals(
                "ambiguous reference does not resolve to forward function declaration",
                "def referenced do\n\n  end",
                resolved.getText()
        );
    }

    public void testAmbiguousRecursiveReference() {
        myFixture.configureByFiles("ambiguous_recursive.ex");
        PsiElement ambiguous = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset())
                .getPrevSibling();

        assertInstanceOf(ambiguous.getFirstChild(), ElixirIdentifier.class);

        PsiReference reference = ambiguous.getReference();

        assertNotNull("`referenced` has no reference", reference);

        PsiElement resolved = reference.resolve();

        assertNotNull("`referenced` not resolved", resolved);
        assertEquals(
                "ambiguous reference does not resolve to recursive function declaration",
                "def referenced do\n    referenced\n\n    a = 1\n  end",
                resolved.getText()
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
        assertEquals("def referenced(true) do\n  end", resolved.getText());
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
                resolved.getText()
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

        assertEquals("Did not resolve to all clauses", 3, resolveResults.length);

        ResolveResult firstResolveResult = resolveResults[0];
        PsiElement firstResolved = firstResolveResult.getElement();

        assertEquals(
                "first ResolveResult is not the true clause",
                "def referenced() do\n" +
                        "    referenced(true)\n" +
                        "\n" +
                        "    a = 1\n" +
                        "  end",
                firstResolved.getText()
        );

        ResolveResult secondResolveResult = resolveResults[1];
        PsiElement secondResolved = secondResolveResult.getElement();

        assertEquals(
                "second ResolveResult is not the true clause",
                "def referenced(true) do\n  end",
                secondResolved.getText()
        );

        assertInstanceOf(secondResolved, NavigatablePsiElement.class);

        NavigatablePsiElement navigatableSecondResolved = (NavigatablePsiElement) secondResolved;

        ItemPresentation secondPresentation = navigatableSecondResolved.getPresentation();

        assertNotNull("second ResolveResult element has no presentation", secondPresentation);

        assertEquals("/src/parentheses_multiple_correct_arity.ex defmodule A", secondPresentation.getLocationString());
        assertEquals("referenced(true)", secondPresentation.getPresentableText());

        ResolveResult thirdResolveResult = resolveResults[2];
        PsiElement thirdResolved = thirdResolveResult.getElement();

        assertEquals(
                "third ResolveResult is not the false clause",
                "def referenced(false) do\n  end",
                thirdResolved.getText()
        );

        assertInstanceOf(thirdResolved, NavigatablePsiElement.class);

        NavigatablePsiElement navigatableThirdResolved = (NavigatablePsiElement) thirdResolved;

        ItemPresentation thirdPresentation = navigatableThirdResolved.getPresentation();

        assertNotNull("third ResolveResult element has no presentation", thirdPresentation);

        assertEquals("/src/parentheses_multiple_correct_arity.ex defmodule A", thirdPresentation.getLocationString());
        assertEquals("referenced(false)", thirdPresentation.getPresentableText());
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

        assertInstanceOf(reference, PsiPolyVariantReference.class);

        PsiPolyVariantReference polyVariantReference = (PsiPolyVariantReference) reference;

        ResolveResult[] resolveResults = polyVariantReference.multiResolve(false);

        assertEquals("Did not resolve to all clauses", 2, resolveResults.length);

        ResolveResult firstResolveResult = resolveResults[0];

        assertFalse("first ResolveResult is a valid result", firstResolveResult.isValidResult());

        PsiElement firstResolved = firstResolveResult.getElement();

        assertEquals(
                "first ResolveResult is not the 0-arity clause",
                "def referenced() do\n" +
                        "    referenced(1)\n" +
                        "\n" +
                        "    a = 1\n" +
                        "  end",
                firstResolved.getText()
        );

        ResolveResult secondResolveResult = resolveResults[1];

        assertTrue("second ResolveResult is not a valid result", secondResolveResult.isValidResult());

        PsiElement secondResolved = secondResolveResult.getElement();

        assertEquals(
                "second ResolveResult is not 1-arity clause",
                "def referenced(_) do\n" +
                        "  end",
                secondResolved.getText()
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
