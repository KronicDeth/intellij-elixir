package org.elixir_lang.reference.callable

import com.intellij.psi.PsiPolyVariantReference
import org.elixir_lang.PlatformTestCase

/**
 * Elixir evaluates a value's parts left to right, so when two matches in one value bind the same name, the later one
 * is what the code after the value reads, and what a part after it reads.
 */
class VariableResolvesToTheLastBindingInAValueTest : PlatformTestCase() {
    fun testAReadAfterACallSeesTheLastArgumentBinding() = assertResolvesToLastBinding("f(x = 1, x = 2)\ny = <caret>x\n")

    fun testAnArgumentSeesTheLastArgumentBindingBeforeIt() = assertResolvesToLastBinding("f(x = 1, x = 2, <caret>x)\n")

    fun testAReadAfterAStringSeesTheLastInterpolationBinding() =
        assertResolvesToLastBinding("\"#{x = 1}#{x = 2}\"\ny = <caret>x\n")

    fun testAReadAfterAMapUpdateSeesTheLastValueBinding() =
        assertResolvesToLastBinding("m = %{}\n%{m | a: x = 1, b: x = 2}\ny = <caret>x\n")

    fun testAReadAfterAListSeesTheLastElementBinding() = assertResolvesToLastBinding("[x = 1, x = 2]\ny = <caret>x\n")

    private fun assertResolvesToLastBinding(text: String) {
        myFixture.configureByText("value.ex", text)
        val reference = myFixture.file.findReferenceAt(myFixture.caretOffset) as PsiPolyVariantReference

        assertEquals(listOf("x = 2"), reference.multiResolve(false).mapNotNull { it.element?.parent?.text })
    }
}
