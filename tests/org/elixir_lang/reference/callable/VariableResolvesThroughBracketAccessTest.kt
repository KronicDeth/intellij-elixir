package org.elixir_lang.reference.callable

import com.intellij.psi.PsiPolyVariantReference
import org.elixir_lang.PlatformTestCase

/**
 * A match inside bracket access binds for the code after it, so a later read must resolve to it: the use scope walk
 * carrying through the brackets is only half of Find Usages and rename, the resolver has to descend into them too.
 * The receiver being indexed is a value, never a pattern, so nothing in it is a declaration.
 */
class VariableResolvesThroughBracketAccessTest : PlatformTestCase() {
    fun testReadAfterBracketAccessResolvesToTheMatchInsideIt() =
        assertResolvesTo(listOf("x = 1"), "_ = m[x = 1]\n<caret>x\n")

    fun testReadAfterModuleAttributeAccessResolvesToTheMatchInsideIt() =
        assertResolvesTo(listOf("x = 1"), "_ = @attr[x = 1]\n<caret>x\n")

    // pins that only the arguments of bracket access are read, not the receiver being indexed
    fun testAReadInTheIndexedExpressionIsNotADeclaration() =
        assertResolvesTo(emptyList(), "run m[k] do\n  :ok\nend\n<caret>m\n")

    private fun assertResolvesTo(declarations: List<String>, text: String) {
        myFixture.configureByText("brackets.ex", text)
        val reference = myFixture.file.findReferenceAt(myFixture.caretOffset) as PsiPolyVariantReference

        assertEquals(declarations, reference.multiResolve(false).mapNotNull { it.element?.parent?.text })
    }
}
