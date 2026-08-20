package org.elixir_lang.heex.html

import com.intellij.psi.templateLanguages.DefaultOuterLanguagePatcher
import junit.framework.TestCase
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.heex.file.psi.TemplateData

/**
 * [HeexHTMLOuterLanguageRangePatcher] is registered for `language="HTML"`, so it runs for every
 * HTML-data template language in the IDE, not just HEEx -
 * [com.intellij.psi.templateLanguages.TemplateDataElementType.OuterLanguageRangePatcher.EXTENSION]
 * has one patcher per language. It must therefore check which [com.intellij.psi.templateLanguages.TemplateDataElementType]
 * the range belongs to, not just which language, and return `null` - "insert nothing" - for anyone
 * else's, leaving other HTML template languages' own (patcher-less) behaviour untouched.
 */
class HeexHTMLOuterLanguageRangePatcherTest : TestCase() {
    private val patcher = HeexHTMLOuterLanguageRangePatcher()

    fun testReturnsThePlatformPlaceholderForHeex() {
        assertEquals(
            DefaultOuterLanguagePatcher.OUTER_EXPRESSION_PLACEHOLDER,
            patcher.getTextForOuterLanguageInsertionRange(TemplateData.INSTANCE, "@x")
        )
    }

    fun testReturnsNullForAnotherHtmlDataTemplateLanguage() {
        val otherTemplateDataElementType = org.elixir_lang.eex.element_type.TemplateData(ElixirLanguage)

        assertNull(patcher.getTextForOuterLanguageInsertionRange(otherTemplateDataElementType, "@x"))
    }
}
