package org.elixir_lang.documentation

import com.intellij.psi.PsiReferenceService
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.ElixirAtom
import org.elixir_lang.reference.MfaFunctionReference

/**
 * Integration tests verifying that hover documentation is produced when hovering over
 * the function atom in an MFA tuple (e.g. `:map` in `{Documented, :map, 2}`).
 *
 * The documentation provider pipeline is exercised end-to-end:
 *   MfaFunctionReference.resolve() → ElixirDocumentationProvider.generateHoverDoc()
 *
 * This validates success criterion #3 of the MFA tuple resolution plan:
 * "Hover on `:map` in `{Enum, :map, 2}` shows @doc for `Enum.map/2`."
 */
class MfaTupleHoverDocumentationTest : PlatformTestCase() {

    fun testHoverDocShowsForMfaFunctionAtom() {
        myFixture.configureByFiles("documented_function.ex")

        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("No element at caret", elementAtCaret)

        val atom = PsiTreeUtil.getParentOfType(elementAtCaret!!, ElixirAtom::class.java, false)
        assertNotNull("No ElixirAtom at caret", atom)

        val mfaRef = PsiReferenceService.getService()
            .getReferences(atom!!, PsiReferenceService.Hints.NO_HINTS)
            .filterIsInstance<MfaFunctionReference>()
            .singleOrNull()
        assertNotNull("No MfaFunctionReference on atom", mfaRef)

        val resolved = mfaRef!!.resolve()
        assertNotNull("MFA reference did not resolve", resolved)

        val hover = ElixirDocumentationProvider().generateHoverDoc(resolved!!, atom)
        assertNotNull("Hover documentation is null - @doc content not found for MFA function reference", hover)

        assertTrue(
            "Expected module name 'Documented' in hover doc, got: $hover",
            hover!!.contains("Documented")
        )
        assertTrue(
            "Expected function head 'map' in hover doc, got: $hover",
            hover.contains("map")
        )
        assertTrue(
            "Expected @doc content in hover doc, got: $hover",
            hover.contains("Maps a function over an enumerable")
        )
    }

    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/documentation/mfa_tuple_hover"
}
