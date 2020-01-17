package org.elixir_lang.reference.callable;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.elixir_lang.psi.call.Call;

public class ImportTest extends LightPlatformCodeInsightFixtureTestCase {
    /*
     * Tests
     */

    public void testImportModule() {
        myFixture.configureByFiles("import_module.ex", "imported.ex");
        PsiElement elementAtCaret = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        assertNotNull(elementAtCaret);

        PsiElement maybeCall = elementAtCaret.getParent().getParent();

        assertInstanceOf(maybeCall, Call.class);

        Call call = (Call) maybeCall;
        assertEquals("imported", call.functionName());
        assertEquals(0, call.resolvedFinalArity());

        PsiReference reference = call.getReference();

        assertNotNull(reference);
        assertInstanceOf(reference, PsiPolyVariantReference.class);

        PsiPolyVariantReference polyVariantReference = (PsiPolyVariantReference) reference;

        ResolveResult[] resolveResults = polyVariantReference.multiResolve(false);

        assertEquals(3, resolveResults.length);

        ResolveResult firstResolveResult = resolveResults[0];

        assertTrue(firstResolveResult.isValidResult());

        PsiElement firstResolved = firstResolveResult.getElement();

        assertEquals("def imported() do\n" +
                "    imported(1)\n" +
                "  end", firstResolved.getText());

        ResolveResult secondResolveResult = resolveResults[1];

        assertTrue(secondResolveResult.isValidResult());

        PsiElement secondResolved = secondResolveResult.getElement();

        assertEquals("import Imported", secondResolved.getText());

        ResolveResult thirdResolveResult = resolveResults[2];

        assertFalse(thirdResolveResult.isValidResult());

        PsiElement thirdResolved = thirdResolveResult.getElement();

        assertEquals("defp imported(1) do\n" +
                "    :ok\n" +
                "  end", thirdResolved.getText());
    }

    public void testImportModuleExceptNameArity() {
        myFixture.configureByFiles("import_module_except_name_arity.ex", "imported.ex");
        PsiElement elementAtCaret = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        assertNotNull(elementAtCaret);

        PsiElement maybeCall = elementAtCaret.getParent().getParent();

        assertInstanceOf(maybeCall, Call.class);

        Call call = (Call) maybeCall;
        assertEquals("imported", call.functionName());
        assertEquals(0, call.resolvedFinalArity());

        PsiReference reference = call.getReference();

        assertNotNull(reference);
        assertInstanceOf(reference, PsiPolyVariantReference.class);

        PsiPolyVariantReference polyVariantReference = (PsiPolyVariantReference) reference;

        ResolveResult[] resolveResults = polyVariantReference.multiResolve(false);

        assertEquals(3, resolveResults.length);

        ResolveResult firstResolveResult = resolveResults[0];

        assertTrue(firstResolveResult.isValidResult());

        PsiElement firstResolved = firstResolveResult.getElement();

        assertEquals("def imported() do\n" +
                "    imported(1)\n" +
                "  end", firstResolved.getText());

        ResolveResult secondResolveResult = resolveResults[1];

        assertTrue(secondResolveResult.isValidResult());

        PsiElement secondResolved = secondResolveResult.getElement();

        assertEquals("import Imported, except: [unimported: 0]", secondResolved.getText());

        ResolveResult thirdResolveResult = resolveResults[2];

        assertFalse(thirdResolveResult.isValidResult());

        PsiElement thirdResolved = thirdResolveResult.getElement();

        assertEquals("defp imported(1) do\n" +
                "    :ok\n" +
                "  end", thirdResolved.getText());
    }

    public void testImportModuleOnlyNameArity() {
        myFixture.configureByFiles("import_module_only_name_arity.ex", "imported.ex");
        PsiElement elementAtCaret = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        assertNotNull(elementAtCaret);

        PsiElement maybeCall = elementAtCaret.getParent().getParent();

        assertInstanceOf(maybeCall, Call.class);

        Call call = (Call) maybeCall;
        assertEquals("imported", call.functionName());
        assertEquals(0, call.resolvedFinalArity());

        PsiReference reference = call.getReference();

        assertNotNull(reference);
        assertInstanceOf(reference, PsiPolyVariantReference.class);

        PsiPolyVariantReference polyVariantReference = (PsiPolyVariantReference) reference;

        ResolveResult[] resolveResults = polyVariantReference.multiResolve(false);

        assertEquals(2, resolveResults.length);
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/callable/import";
    }
}
