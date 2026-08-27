package org.elixir_lang.code_insight.lookup.element_renderer

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementPresentation
import org.elixir_lang.PlatformTestCase

/**
 * Characterises the *tail text* of variable completion, which nothing else asserts:
 * `psi/scope/variable/VariantsTest` checks only which names are offered, never how one renders.
 *
 * [Variable] appends the enclosing match after the name so a completion item shows where the
 * variable was bound. That is useful when there is a match to show, and redundant when there is
 * not - a bare parameter is its own enclosing match, so the name would render twice. Both halves
 * are pinned here; dropping the second would let a later cleanup delete the useful case.
 */
class VariableTest : PlatformTestCase() {
    fun testBareParameterHasNoRedundantTailText() {
        val presentation = presentationOf("count")

        assertEquals("count", presentation.itemText)
        assertEquals(
            "A bare parameter is its own enclosing match, so repeating it as tail text just shows " +
                    "the name twice, got: ${presentation.tailFragments}",
            "",
            presentation.tailText.orEmpty().trim()
        )
    }

    fun testMatchedParameterKeepsItsMatchAsTailText() {
        val presentation = presentationOf("account")

        assertEquals("account", presentation.itemText)
        assertEquals(
            "A parameter bound by a match must still show that match, got: ${presentation.tailFragments}",
            "%{amount: amount} = account",
            presentation.tailText.orEmpty().trim()
        )
    }

    fun testMatchedVariableKeepsItsMatchAsTailText() {
        val presentation = presentationOf("total")

        assertEquals("total", presentation.itemText)
        assertEquals(
            "A variable bound by a match must still show that match, got: ${presentation.tailFragments}",
            "total = 1",
            presentation.tailText.orEmpty().trim()
        )
    }

    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/code_insight/lookup/element_renderer/variable"

    /**
     * Drives the real completion popup and renders the element offered for [name], so the assertions
     * run against what a user sees rather than a hand-built lookup element.
     */
    private fun presentationOf(name: String): LookupElementPresentation {
        myFixture.configureByFile("issue_496.ex")
        myFixture.complete(CompletionType.BASIC)

        val elements = myFixture.lookupElements
        assertNotNull("Completion not shown", elements)

        val element = elements!!.firstOrNull { it.lookupString == name }
        assertNotNull("$name was not offered, got: ${elements.map { it.lookupString }}", element)

        return LookupElementPresentation.renderElement(element!!)
    }
}
