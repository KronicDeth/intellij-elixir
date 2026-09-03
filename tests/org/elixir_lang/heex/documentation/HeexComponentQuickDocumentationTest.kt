package org.elixir_lang.heex.documentation

import org.elixir_lang.documentation.quickDocumentationAtCaret
import org.elixir_lang.heex.reference.HeexHostTestCase
import org.elixir_lang.heex.reference.fixtureText
import org.elixir_lang.heex.reference.heexSigilModuleText

/**
 * Ctrl+Q on a HEEx component tag shows the `@doc` of the `def`/`defp` the tag names, in a `.heex`
 * file and inside a `~H` sigil.
 *
 * A component tag resolves to the clause's *name identifier* rather than the clause itself -
 * `HeexComponentResolver.declarationTarget` narrows it so Rename and Go To Declaration land on a
 * `PsiNamedElement` - so this locks the documentation pipeline's ability to render docs from that
 * name identifier. Driving the real gesture rather than calling a provider directly keeps the test
 * valid however the target is produced.
 */
class HeexComponentQuickDocumentationTest : HeexHostTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/heex/documentation"

    fun testComponentTagInHeexFileShowsAtDoc() {
        myFixture.configureByFiles("component/page_live.html.heex", "component/page_live.ex")

        assertRendersButtonDoc(myFixture.quickDocumentationAtCaret(project))
    }

    fun testLocalComponentTagInsideHSigilShowsAtDoc() {
        myFixture.configureByText(
            "test.ex",
            heexSigilModuleText(
                entranceModuleText = myFixture.fixtureText("component/page_live.ex"),
                heexBody = myFixture.fixtureText("component/page_live.html.heex")
            )
        )

        assertRendersButtonDoc(myFixture.quickDocumentationAtCaret(project))
    }

    fun testRemoteComponentTagInsideHSigilShowsAtDoc() {
        myFixture.configureByText(
            "test.ex",
            heexSigilModuleText(
                entranceModuleText = "defmodule Test do\nend",
                heexBody = myFixture.fixtureText("remote/page_live.html.heex"),
                myFixture.fixtureText("remote/core_components.ex")
            )
        )

        assertRendersButtonDoc(myFixture.quickDocumentationAtCaret(project))
    }

    /** An unresolved tag declares itself, so there is no clause to document. */
    fun testUnresolvedComponentTagShowsNoAtDoc() {
        myFixture.configureByText("unresolved.html.heex", "<.nonexis<caret>tent>Click</.nonexistent>")

        val documentation = myFixture.quickDocumentationAtCaret(project)

        assertFalse(
            "An unresolved component tag should show no @doc, got: $documentation",
            documentation.orEmpty().contains("clickable button")
        )
    }

    private fun assertRendersButtonDoc(documentation: String?) {
        assertNotNull("Quick Documentation should be shown for a resolved component tag", documentation)
        assertTrue(
            "Expected the function head in the documentation, got: $documentation",
            documentation!!.contains("button")
        )
        assertTrue(
            "Expected the @doc body in the documentation, got: $documentation",
            documentation.contains("clickable button")
        )
    }
}
