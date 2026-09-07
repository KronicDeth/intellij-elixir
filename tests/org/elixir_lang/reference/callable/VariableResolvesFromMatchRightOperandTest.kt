package org.elixir_lang.reference.callable

import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveResult
import org.elixir_lang.PlatformTestCase

/**
 * A read on the right of `=` is bound by the earlier binding of its name, not by the match it sits in, so it must
 * resolve to that binding wherever the match is and whatever else is on the right. Whether the name is bound is
 * decided the same way in complete and incomplete code; incomplete code only adds prefix-named candidates.
 */
class VariableResolvesFromMatchRightOperandTest : PlatformTestCase() {
    fun testABareRightOperandAtTopLevelResolvesToTheEarlierBinding() = assertResolvesToBinding("x = 1\ny = <caret>x\n")

    fun testABareRightOperandInADefinitionResolvesToTheEarlierBinding() =
        assertResolvesToBinding("defmodule M do\n  def f do\n    x = 1\n    y = <caret>x\n  end\nend\n")

    fun testAReadInsideARightOperandTupleResolvesToTheEarlierBinding() =
        assertResolvesToBinding("x = 1\ny = {<caret>x, 2}\n")

    fun testAPrefixNamedEarlierVariableDoesNotBindAnUnboundReadInIncompleteCode() =
        assertResolvesInIncompleteCode("xy = 1\ny = x\nz = <caret>x\n", "x in y = x", "xy in xy = 1 (candidate)")

    fun testAPrefixNamedReadOnTheRightStaysACandidateWhenTheSearchedNameIsBound() =
        assertResolvesInIncompleteCode("x = 1\nw = xy\nz = <caret>x\n", "x in x = 1", "xy in w = xy (candidate)")

    private fun assertResolvesToBinding(text: String) {
        myFixture.configureByText("match.ex", text)
        val reference = myFixture.file.findReferenceAt(myFixture.caretOffset) as PsiPolyVariantReference

        assertEquals(listOf("x = 1"), reference.multiResolve(false).mapNotNull { it.element?.parent?.text })
    }

    private fun assertResolvesInIncompleteCode(text: String, vararg expected: String) {
        myFixture.configureByText("match.ex", text)
        val reference = myFixture.file.findReferenceAt(myFixture.caretOffset) as PsiPolyVariantReference

        assertEquals(expected.toSet(), reference.multiResolve(true).map { it.describe() }.toSet())
    }

    private fun ResolveResult.describe(): String =
        "${element!!.text} in ${element!!.parent.text}" + if (isValidResult) "" else " (candidate)"
}
