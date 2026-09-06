package org.elixir_lang.reference.callable

import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.call.Call

/**
 * A call's arguments are evaluated left to right, so a match in an earlier argument binds for a later one and for the
 * code after the call, while a later argument binds nothing for an earlier read.
 */
class CallArgumentBindingTest : PlatformTestCase() {
    fun testReadResolvesToABindingInAnEarlierArgument() {
        myFixture.configureByText("earlier.ex", "def f do\n  IO.puts(y = 1, <caret>y)\nend\n")
        val read = callAtCaret()

        val elements = (read.reference as PsiPolyVariantReference).multiResolve(false).mapNotNull { it.element }

        assertTrue("expected the `y` bound in the first argument, got $elements", elements.any { it.textOffset < read.textOffset })
    }

    fun testReadDoesNotResolveToABindingInALaterArgument() {
        myFixture.configureByText("later.ex", "def f do\n  IO.puts(<caret>y, y = 1)\nend\n")
        val read = callAtCaret()

        val elements = (read.reference as PsiPolyVariantReference).multiResolve(false).mapNotNull { it.element }

        assertFalse("a later argument bound the read: $elements", elements.any { it.textOffset > read.textOffset })
    }

    private fun callAtCaret(): Call {
        val call = PsiTreeUtil.getParentOfType(myFixture.file.findElementAt(myFixture.caretOffset), Call::class.java)
        assertNotNull("caret is not on a call", call)

        return call!!
    }
}
