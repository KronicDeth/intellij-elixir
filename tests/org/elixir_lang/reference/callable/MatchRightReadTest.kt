package org.elixir_lang.reference.callable

import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.call.Call

/**
 * A read on the right of a match is never its own resolve result. With nothing bound above it, resolution falls back
 * to a variable bound inside a `quote` in another module; a pattern binding under a match still resolves to itself.
 */
class MatchRightReadTest : PlatformTestCase() {
    fun testUnboundReadIsNotItsOwnResult() {
        myFixture.configureByFiles("unbound.ex")
        val read = callAtCaret()

        for (incompleteCode in listOf(false, true)) {
            val elements = (read.reference as PsiPolyVariantReference).multiResolve(incompleteCode).map { it.element }

            assertFalse("incompleteCode=$incompleteCode resolved the read to itself", read in elements)
        }
    }

    fun testUnboundReadFallsBackToAQuoteBoundVariable() {
        myFixture.configureByFiles("without_use.ex", "quoted.ex")
        val read = callAtCaret()

        val elements = (read.reference as PsiPolyVariantReference).multiResolve(false).mapNotNull { it.element }

        assertTrue(
            "expected the `injected` bound inside the quote, got ${elements.map { it.containingFile.name + ":" + it.text }}",
            elements.any { it.containingFile.name == "quoted.ex" && it.text == "injected" }
        )
    }

    fun testReadOfAPatternBoundUnderAMatchResolvesToThePattern() {
        myFixture.configureByFiles("pattern_under_match.ex")
        val read = callAtCaret()

        val elements = (read.reference as PsiPolyVariantReference).multiResolve(false).mapNotNull { it.element }

        assertTrue(
            "expected the `x` in the case pattern, got ${elements.map { it.textOffset.toString() + ":" + it.text }}",
            elements.any { it.text == "x" && it.textOffset < read.textOffset }
        )
    }

    private fun callAtCaret(): Call {
        val call = PsiTreeUtil.getParentOfType(myFixture.file.findElementAt(myFixture.caretOffset), Call::class.java)
        assertNotNull("caret is not on a call", call)

        return call!!
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/callable/match_right_read"
}
