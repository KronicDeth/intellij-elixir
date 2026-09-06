package org.elixir_lang.psi.scope

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.ResolveState
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.ElixirHeredoc

/** The resolver enters a string only when it can hold an interpolation, so a long heredoc costs one visit. */
class VariableDescentThroughStringTest : PlatformTestCase() {
    fun testHeredocWithoutInterpolationIsNotEntered() {
        val visits = visitsOf("_ = \"\"\"\nline one\nline two\nline three\n\"\"\"\n")

        assertEquals(1, visits)
    }

    fun testHeredocWithInterpolationIsEntered() {
        val visits = visitsOf("_ = \"\"\"\nline one\nline #{x = 1}\nline three\n\"\"\"\n")

        assertTrue("the heredoc was not entered: $visits visits", visits > 1)
    }

    private fun visitsOf(text: String): Int {
        myFixture.configureByText("heredoc.ex", text)
        val heredoc = PsiTreeUtil.findChildOfType(myFixture.file, ElixirHeredoc::class.java)
        assertNotNull("no heredoc parsed", heredoc)

        val counting = object : Variable() {
            var visits = 0

            override fun execute(element: PsiElement, state: ResolveState): Boolean {
                visits += 1
                return super.execute(element, state)
            }

            override fun executeOnVariable(match: PsiNamedElement, state: ResolveState): Boolean = true
        }
        counting.execute(heredoc!!, ResolveState.initial())

        return counting.visits
    }
}
