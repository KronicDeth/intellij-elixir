package org.elixir_lang.eex.file

import com.intellij.codeInsight.template.CustomLiveTemplate
import com.intellij.codeInsight.template.CustomTemplateCallback
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.fileTypes.PlainTextLanguage
import org.elixir_lang.PlatformTestCase

/**
 * https://github.com/KronicDeth/intellij-elixir/issues/1426
 *
 * Emmet resolves its context by language, through the leaf the multi-rooted view provider returns
 * for the caret, so in a template it depends on which root claims that offset rather than on which
 * IDE is running. `*.html.eex` gets an HTML root and Emmet is applicable throughout - including
 * between the tags of an Elixir `do` block, the position that makes a Phoenix template look like it
 * behaves differently line by line. A bare `*.eex` has no base extension to derive a data language
 * from, falls back to plain text, and so is the one shape where Emmet really is unavailable.
 *
 * Emmet is reached through [CustomLiveTemplate.EP_NAME] rather than by naming `ZenCodingTemplate`:
 * its classes sat in `intellij.xml.impl` up to 2025.3 and moved to their own `intellij.xml.emmet`
 * module in 2026.1, which is not on this project's compile classpath. The extension point is
 * platform API and is stable across both.
 *
 * [testEmmetIsApplicableInAPlainHtmlFile] is a control: without it, every other assertion here
 * would pass for the wrong reason if Emmet were absent.
 */
class EmmetApplicabilityTest : PlatformTestCase() {
    fun testEmmetIsApplicableInAPlainHtmlFile() {
        assertTrue(
            "Emmet is not applicable in a plain HTML file, so nothing else here is meaningful",
            applicable("control.html", "<div>\n  div.foo<caret>\n</div>")
        )
    }

    fun testEmmetIsApplicableInMarkup() {
        assertTrue(applicable("page.html.eex", "<div>\n  div.foo<caret>\n</div>"))
    }

    fun testEmmetIsApplicableInsideAnElixirDoBlock() {
        assertTrue(
            "the markup inside `<%= ... do %> ... <% end %>` is still HTML to Emmet",
            applicable("page.html.eex", "<%= form_for @cs do %>\n  div.foo<caret>\n<% end %>")
        )
    }

    fun testEmmetIsApplicableBetweenTwoEexTags() {
        assertTrue(applicable("page.html.eex", "<%= @a %>\n  ul>li*2<caret>\n<%= @b %>"))
    }

    fun testEmmetIsApplicableInsideATagWithAnEexAttributeValue() {
        assertTrue(applicable("page.html.eex", "<div class=\"<%= @c %>\">\n  div.foo<caret>\n</div>"))
    }

    fun testEmmetReadsTheAbbreviationItWouldExpand() {
        myFixture.configureByText("page.html.eex", "<div>\n  ul>li*2<caret>\n</div>")

        assertEquals("ul>li*2", emmet().computeTemplateKey(callback()))
    }

    fun testTheContextInsideAnEexTemplateIsXml() {
        myFixture.configureByText("page.html.eex", "<div>\n  div.foo<caret>\n</div>")

        assertInstanceOf(callback().context.language, XMLLanguage::class.java)
    }

    fun testEmmetIsUnavailableInABareEexWhichHasNoDataLanguage() {
        myFixture.configureByText("page.eex", "<div>\n  div.foo<caret>\n</div>")

        assertEquals(
            "a bare `.eex` falls back to plain text, which is why Emmet has no context there",
            PlainTextLanguage.INSTANCE,
            callback().context.language
        )
        assertFalse(emmet().isApplicable(callback(), myFixture.caretOffset, false))
    }

    private fun applicable(fileName: String, text: String): Boolean {
        myFixture.configureByText(fileName, text)

        return emmet().isApplicable(callback(), myFixture.caretOffset, false)
    }

    private fun callback() = CustomTemplateCallback(myFixture.editor, myFixture.file)

    private fun emmet(): CustomLiveTemplate =
        CustomLiveTemplate.EP_NAME.extensionList.singleOrNull { it.javaClass.simpleName == "ZenCodingTemplate" }
            ?: throw AssertionError(
                "Emmet is not registered in this fixture. Registered custom live templates: " +
                        CustomLiveTemplate.EP_NAME.extensionList.map { it.javaClass.name }
            )
}
