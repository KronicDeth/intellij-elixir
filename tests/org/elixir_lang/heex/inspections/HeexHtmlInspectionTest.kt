package org.elixir_lang.heex.inspections

import com.intellij.codeInspection.htmlInspections.HtmlUnknownAttributeInspection
import com.intellij.codeInspection.htmlInspections.HtmlUnknownBooleanAttributeInspection
import com.intellij.codeInspection.htmlInspections.HtmlUnknownTagInspection
import org.elixir_lang.PlatformTestCase

/**
 * End-to-end tests through the real inspection engine, on `.html.heex` fixtures only. A `~H`
 * sigil's HTML root has an injection host, which downgrades `HtmlUnknownTag`/`HtmlUnknownAttribute`
 * to a non-visible severity ([com.intellij.xml.util.XmlUtil.isNotInjectedOrCustomHtmlFile] and
 * `XmlHighlightVisitor.isInjectedWithoutValidation`) - a real `.heex` file has no such host, so it
 * is the fixture that actually exercises `GENERIC_ERROR_OR_WARNING`.
 *
 * The two `StillFlagged` tests are controls: without them every `NotFlagged` assertion would pass
 * vacuously if the inspections did not run inside a multi-root HEEx view provider at all.
 */
class HeexHtmlInspectionTest : PlatformTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(HtmlUnknownAttributeInspection(), HtmlUnknownTagInspection(), HtmlUnknownBooleanAttributeInspection())
    }

    fun testUnknownAttributeOnPlainHtmlTagStillFlagged() {
        assertHasProblemMentioning("bogus-attribute", "<div bogus-attribute=\"x\">y</div>")
    }

    fun testUnknownTagStillFlagged() {
        assertHasProblemMentioning("bogustag", "<bogustag>y</bogustag>")
    }

    fun testSpecialAttributesOnPlainHtmlTagNotFlagged() {
        assertNoProblemMentioning(
            listOf(":for", ":if", ":key"),
            "<table><tbody><tr><th :for={col <- @col} :if={@show} :key={col.id}>x</th></tr></tbody></table>"
        )
    }

    fun testPhxBindingsOnPlainHtmlTagNotFlagged() {
        assertNoProblemMentioning(
            listOf("phx-click", "phx-value-id", "phx-update", "phx-hook"),
            "<div id=\"a\" phx-click=\"go\" phx-value-id=\"1\" phx-update=\"stream\" phx-hook=\"H\">x</div>"
        )
    }

    fun testValuelessPhxBindingNotFlaggedAsNonBoolean() {
        assertNoProblemMentioning(listOf("phx-no-format"), "<div phx-no-format>x</div>")
    }

    fun testSlotTagNotFlaggedAsUnknownTag() {
        assertNoProblemMentioning(
            listOf(":action"),
            "<.table rows={@rows}><:action :let={r}>x</:action></.table>"
        )
    }

    fun testSlotTagAttributesNotFlagged() {
        assertNoProblemMentioning(
            listOf("label"),
            "<.table rows={@rows}><:col label=\"Name\" :let={r}>x</:col></.table>"
        )
    }

    fun testTagInsideSlotNotFlagged() {
        assertNoProblemMentioning(
            listOf("span"),
            "<.table rows={@rows}><:col :let={r}><span>x</span></:col></.table>"
        )
    }

    /**
     * An unresolved but syntactically valid component tag must not become "Cannot resolve symbol" -
     * that comes from `XmlHighlightVisitor`'s reference check, not `HtmlUnknownTagInspection`, so no
     * `InspectionSuppressor` can silence it once a real (non-`Any`) descriptor exists for the tag.
     */
    fun testUnresolvedComponentNotFlaggedAsUnresolvedReference() {
        assertNoProblemMentioning(listOf("nonexistent"), "<.nonexistent>Click</.nonexistent>")
    }

    private fun assertHasProblemMentioning(name: String, text: String) {
        myFixture.configureByText("x.html.heex", text)
        val descriptions = myFixture.doHighlighting().mapNotNull { it.description }
        assertTrue("expected a problem mentioning '$name', got: $descriptions", descriptions.any { name in it })
    }

    private fun assertNoProblemMentioning(names: List<String>, text: String) {
        myFixture.configureByText("x.html.heex", text)
        val descriptions = myFixture.doHighlighting().mapNotNull { it.description }
        for (name in names) {
            assertFalse("did not expect a problem mentioning '$name', got: $descriptions", descriptions.any { name in it })
        }
    }
}
