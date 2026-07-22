package org.elixir_lang.heex

import com.intellij.lang.html.HTMLLanguage
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.psi.templateLanguages.ConfigurableTemplateLanguageFileViewProvider
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.eex.Language as EExLanguage

class HEExFileTypeTest : PlatformTestCase() {

    fun testFileTypeRegistered() {
        val fileType = FileTypeManager.getInstance().getFileTypeByExtension("heex")
        assertInstanceOf(fileType, org.elixir_lang.heex.file.Type::class.java)
        assertEquals("HEEx", fileType.name)
    }

    fun testFileTypeProperties() {
        val fileType = org.elixir_lang.heex.file.Type.INSTANCE
        assertEquals("HEEx", fileType.name)
        assertEquals("heex", fileType.defaultExtension)
        assertNotNull(fileType.icon)
    }

    fun testDefaultTemplateDataLanguageIsHtml() {
        val fileType = org.elixir_lang.heex.file.Type.INSTANCE as org.elixir_lang.heex.file.Type
        assertEquals(HTMLLanguage.INSTANCE, fileType.defaultTemplateDataLanguage())
    }

    fun testEexDefaultTemplateDataLanguageIsNull() {
        val fileType = org.elixir_lang.eex.file.Type.INSTANCE as org.elixir_lang.eex.file.Type
        assertNull(fileType.defaultTemplateDataLanguage())
    }

    fun testViewProviderUsesHtmlForHeexFile() {
        val file = myFixture.configureByText("template.heex", "<div><%= @name %></div>")
        val viewProvider = file.viewProvider

        assertInstanceOf(viewProvider, ConfigurableTemplateLanguageFileViewProvider::class.java)
        val templateViewProvider = viewProvider as ConfigurableTemplateLanguageFileViewProvider
        assertEquals(HTMLLanguage.INSTANCE, templateViewProvider.templateDataLanguage)
    }

    fun testViewProviderLanguagesIncludeElixir() {
        val file = myFixture.configureByText("template.heex", "<div><%= @name %></div>")
        val languages = file.viewProvider.languages

        assertTrue("Should contain EEx language", languages.any { it.isKindOf(EExLanguage.INSTANCE) })
        assertTrue("Should contain HTML language", languages.any { it.isKindOf(HTMLLanguage.INSTANCE) })
        assertTrue("Should contain Elixir language", languages.any { it.isKindOf(ElixirLanguage.INSTANCE) })
    }

    fun testEexFileDoesNotDefaultToHtml() {
        val file = myFixture.configureByText("template.eex", "<%= @name %>")
        val viewProvider = file.viewProvider

        assertInstanceOf(viewProvider, ConfigurableTemplateLanguageFileViewProvider::class.java)
        val templateViewProvider = viewProvider as ConfigurableTemplateLanguageFileViewProvider
        // Plain .eex without double extension should NOT be HTML
        assertFalse(
            "Plain .eex should not default to HTML",
            templateViewProvider.templateDataLanguage == HTMLLanguage.INSTANCE
        )
    }

    fun testHtmlEexFileUsesHtml() {
        val file = myFixture.configureByText("template.html.eex", "<div><%= @name %></div>")
        val viewProvider = file.viewProvider

        assertInstanceOf(viewProvider, ConfigurableTemplateLanguageFileViewProvider::class.java)
        val templateViewProvider = viewProvider as ConfigurableTemplateLanguageFileViewProvider
        assertEquals(HTMLLanguage.INSTANCE, templateViewProvider.templateDataLanguage)
    }

    fun testHeexWithEexTags() {
        val file = myFixture.configureByText(
            "page.heex",
            """
            <div class="container">
              <%= if @show do %>
                <p><%= @message %></p>
              <% end %>
            </div>
            """.trimIndent()
        )
        assertNotNull(file)
        assertFalse(file.text.isEmpty())
    }

    // ------------------------------------------------------------------
    // HEEx-specific syntax patterns (from Phoenix LiveView assigns-eex guide).
    // These use curly-brace expressions that our EEx parser does not handle
    // natively -- they are treated as HTML text. These tests verify the files
    // still parse without exceptions.
    // ------------------------------------------------------------------

    fun testHeexCurlyBraceAssignExpression() {
        // {@ ...} assign access syntax
        assertHeexParses(
            """
            <div id={"user_#{@user.id}"}>
              {@user.name}
            </div>
            """.trimIndent()
        )
    }

    fun testHeexCurlyBraceFunctionCall() {
        // {function(...)} expression syntax
        assertHeexParses(
            """
            <h1>{expand_title(@title)}</h1>
            """.trimIndent()
        )
    }

    fun testHeexFunctionComponent() {
        // <.component /> syntax
        assertHeexParses(
            """
            <.show_name name={@user.name} />
            """.trimIndent()
        )
    }

    fun testHeexFunctionComponentWithBody() {
        // <.component>...</.component> syntax with inner block
        assertHeexParses(
            """
            <div class="card">
              <.card_header title={@title} class={@title_class} />
              <.card_body>
                {render_slot(@inner_block)}
              </.card_body>
              <.card_footer on_close={@on_close} />
            </div>
            """.trimIndent()
        )
    }

    fun testHeexAssignsSpread() {
        // {assigns} spread syntax
        assertHeexParses(
            """
            <div class="card">
              <.card_header {assigns} />
            </div>
            """.trimIndent()
        )
    }

    fun testHeexForComprehension() {
        // :for special attribute
        assertHeexParses(
            """
            <section :for={post <- @posts}>
              <h1>{expand_title(post.title)}</h1>
            </section>
            """.trimIndent()
        )
    }

    fun testHeexForComprehensionWithKey() {
        // :for + :key special attributes
        assertHeexParses(
            """
            <section :for={post <- @posts} :key={post.id}>
              <h1>{expand_title(post.title)}</h1>
            </section>
            """.trimIndent()
        )
    }

    fun testHeexEexForComprehension() {
        // Traditional EEx for comprehension (still supported in HEEx)
        assertHeexParses(
            """
            <%= for post <- @posts do %>
              <section>
                <h1>{expand_title(post.title)}</h1>
              </section>
            <% end %>
            """.trimIndent()
        )
    }

    fun testHeexMixedSyntax() {
        // Realistic template mixing EEx tags and HEEx curly-brace expressions
        assertHeexParses(
            """
            <div class="container">
              <%= if @show do %>
                <.header title={@page_title} />
                <section :for={item <- @items} :key={item.id}>
                  <p>{item.name}</p>
                  <p><%= item.description %></p>
                </section>
              <% end %>
            </div>
            """.trimIndent()
        )
    }

    private fun assertHeexParses(content: String) {
        val file = myFixture.configureByText("template.heex", content)
        assertNotNull("File should be created", file)
        assertFalse("File should not be empty", file.text.isEmpty())

        val viewProvider = file.viewProvider
        assertInstanceOf(viewProvider, ConfigurableTemplateLanguageFileViewProvider::class.java)
        assertEquals(
            HTMLLanguage.INSTANCE,
            (viewProvider as ConfigurableTemplateLanguageFileViewProvider).templateDataLanguage
        )
    }
}
