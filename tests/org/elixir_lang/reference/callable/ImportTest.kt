package org.elixir_lang.reference.callable

import com.intellij.psi.PsiPolyVariantReference
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.call.Call

class ImportTest : PlatformTestCase() {
    fun testImportModule() {
        myFixture.configureByFiles("import_module.ex", "imported.ex")
        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull(elementAtCaret)

        val maybeCall = elementAtCaret!!.parent.parent
        assertInstanceOf(maybeCall, Call::class.java)
        val call = maybeCall as Call
        assertEquals("imported", call.functionName())
        assertEquals(0, call.resolvedFinalArity())

        val reference = call.reference
        assertNotNull(reference)
        assertInstanceOf(reference, PsiPolyVariantReference::class.java)
        val polyVariantReference = reference as PsiPolyVariantReference?

        val resolveResults = polyVariantReference!!.multiResolve(false)
        assertEquals(2, resolveResults.size)

        val firstResolveResult = resolveResults[0]
        assertTrue(firstResolveResult.isValidResult)
        val firstResolved = firstResolveResult.element
        assertEquals("""def imported() do
    imported(1)
  end""", firstResolved!!.text)

        val secondResolveResult = resolveResults[1]
        assertTrue(secondResolveResult.isValidResult)
        val secondResolved = secondResolveResult.element
        assertEquals("import Imported", secondResolved!!.text)
    }

    fun testImportModuleExceptNameArity() {
        myFixture.configureByFiles("import_module_except_name_arity.ex", "imported.ex")

        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull(elementAtCaret)

        val maybeCall = elementAtCaret!!.parent.parent
        assertInstanceOf(maybeCall, Call::class.java)
        val call = maybeCall as Call
        assertEquals("imported", call.functionName())
        assertEquals(0, call.resolvedFinalArity())

        val reference = call.reference
        assertNotNull(reference)
        assertInstanceOf(reference, PsiPolyVariantReference::class.java)
        val polyVariantReference = reference as PsiPolyVariantReference?

        val resolveResults = polyVariantReference!!.multiResolve(false)
        assertEquals(2, resolveResults.size)

        val firstResolveResult = resolveResults[0]
        assertTrue(firstResolveResult.isValidResult)
        val firstResolved = firstResolveResult.element
        assertEquals("""def imported() do
    imported(1)
  end""", firstResolved!!.text)

        val secondResolveResult = resolveResults[1]
        assertTrue(secondResolveResult.isValidResult)
        val secondResolved = secondResolveResult.element
        assertEquals("import Imported, except: [unimported: 0]", secondResolved!!.text)
    }

    fun testImportModuleOnlyNameArity() {
        myFixture.configureByFiles("import_module_only_name_arity.ex", "imported.ex")

        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull(elementAtCaret)

        val maybeCall = elementAtCaret!!.parent.parent
        assertInstanceOf(maybeCall, Call::class.java)
        val call = maybeCall as Call
        assertEquals("imported", call.functionName())
        assertEquals(0, call.resolvedFinalArity())

        val reference = call.reference
        assertNotNull(reference)
        assertInstanceOf(reference, PsiPolyVariantReference::class.java)
        val polyVariantReference = reference as PsiPolyVariantReference?

        val resolveResults = polyVariantReference!!.multiResolve(false)
        assertEquals(2, resolveResults.size)
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/callable/import"
}
