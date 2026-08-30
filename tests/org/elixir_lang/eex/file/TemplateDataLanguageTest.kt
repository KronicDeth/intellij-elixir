package org.elixir_lang.eex.file

import com.intellij.lang.html.HTMLLanguage
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.psi.templateLanguages.TemplateDataLanguageMappings
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.PlatformTestCase

/**
 * https://github.com/KronicDeth/intellij-elixir/issues/1387
 *
 * `Type.onlyTemplateDataFileType` picks the language for the markup around `<% %>` by stripping the
 * `.eex` suffix and asking what the remaining name is, so `page.html.eex` gets HTML and a bare
 * `page.eex` falls back to `Language.defaultTemplateLanguageFileType()`, which is plain text. EEx is
 * absent from Template Data Languages because it implements `TemplateLanguage` and the platform
 * offers only data languages there.
 */
class TemplateDataLanguageTest : PlatformTestCase() {
    fun testBaseExtensionSelectsTheTemplateDataLanguage() {
        myFixture.configureByText("page.html.eex", "<h1><%= @greeting %></h1>")

        assertEquals(
                "`page.html.eex` did not derive HTML from its base extension",
                HTMLLanguage.INSTANCE,
                templateDataLanguage()
        )
    }

    fun testWithoutABaseExtensionTheDataLanguageFallsBackToPlainText() {
        myFixture.configureByText("page.eex", "<h1><%= @greeting %></h1>")

        assertEquals(
                "a bare `.eex` should fall back to plain text, so the markup around the tags is not highlighted",
                PlainTextLanguage.INSTANCE,
                templateDataLanguage()
        )
    }

    fun testTheElixirInTagsIsParsedWhicheverDataLanguageIsChosen() {
        for (fileName in listOf("page.html.eex", "page.eex")) {
            myFixture.configureByText(fileName, "<h1><%= @greeting %></h1>")

            val viewProvider = myFixture.file.viewProvider

            assertEquals(
                    "$fileName is not an EEx file",
                    org.elixir_lang.eex.Language.INSTANCE,
                    viewProvider.baseLanguage
            )
            assertNotNull(
                    "$fileName has no Elixir root, so `<% %>` would not be highlighted",
                    viewProvider.getPsi(ElixirLanguage)
            )
        }
    }

    private fun templateDataLanguage(): com.intellij.lang.Language {
        val viewProvider = myFixture.file.viewProvider
        assertInstanceOf(viewProvider, TemplateLanguageFileViewProvider::class.java)

        return (viewProvider as TemplateLanguageFileViewProvider).templateDataLanguage
    }

    fun testEmbeddedElixirIsNotOfferedAsATemplateDataLanguage() {
        val templateable = TemplateDataLanguageMappings.getTemplateableLanguages()

        assertFalse(
                "EEx is a TemplateLanguage, so it must not appear in Template Data Languages",
                templateable.contains(org.elixir_lang.eex.Language.INSTANCE)
        )
        assertTrue(
                "HTML should be offered as a template data language",
                templateable.contains(HTMLLanguage.INSTANCE)
        )
    }
}
