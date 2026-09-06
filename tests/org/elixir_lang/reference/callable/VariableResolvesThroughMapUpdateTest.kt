package org.elixir_lang.reference.callable

import com.intellij.psi.PsiPolyVariantReference
import org.elixir_lang.PlatformTestCase

/**
 * A match inside map update arguments binds for the code after the map, so a later read must resolve to it. An
 * update is a value, not a pattern, so the resolver reads it with declaring off and lets the match turn it on.
 */
class VariableResolvesThroughMapUpdateTest : PlatformTestCase() {
    fun testReadAfterMapUpdateResolvesToTheMatchInsideIt() {
        myFixture.configureByText("update.ex", "m = %{k: 0}\n_ = %{m | k: x = 1}\n<caret>x\n")
        val reference = myFixture.file.findReferenceAt(myFixture.caretOffset) as PsiPolyVariantReference

        assertEquals(listOf("x = 1"), reference.multiResolve(false).mapNotNull { it.element?.parent?.text })
    }
}
