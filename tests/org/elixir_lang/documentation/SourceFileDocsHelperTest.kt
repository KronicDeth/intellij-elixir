package org.elixir_lang.documentation

import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.ElixirFileType
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.impl.identifierName

/**
 * Pins that `@typedoc`/`@doc` written as a single-line string, rather than a heredoc, produces
 * documentation.
 *
 * Only the heredoc shape was ever modelled, so a single-line documentation string reached a
 * `TODO()` and threw `kotlin.NotImplementedError` while Quick Documentation was being rendered.
 * `documentationMarkdownText()` now models it with an `ElixirLine` branch; deleting that branch
 * sends the value back to the `else` and these tests fail on a null result.
 */
class SourceFileDocsHelperTest : PlatformTestCase() {
    fun testTypeDocFromSingleLineString() {
        val docs = fetchDocsForModuleAttribute(
            """
            defmodule SingleLineTypeDoc do
              @typedoc "The result of an operation."
              @type t :: :ok | :error
            end
            """.trimIndent(),
            attributeName = "type"
        )

        val typeDocumentation = assertInstanceOf(docs, FetchedDocs.TypeDocumentation::class.java)

        assertEquals("SingleLineTypeDoc", typeDocumentation.module)
        assertEquals("The result of an operation.", typeDocumentation.typedoc)
    }

    fun testCallbackDocFromSingleLineString() {
        val docs = fetchDocsForModuleAttribute(
            """
            defmodule SingleLineCallbackDoc do
              @doc "Handles one message."
              @callback handle(term()) :: :ok
            end
            """.trimIndent(),
            attributeName = "callback"
        )

        val callbackDocumentation = assertInstanceOf(docs, FetchedDocs.CallbackDocumentation::class.java)

        assertEquals("SingleLineCallbackDoc", callbackDocumentation.module)
        assertEquals("Handles one message.", callbackDocumentation.doc)
    }

    fun testTypeDocFromHeredoc() {
        val docs = fetchDocsForModuleAttribute(
            """
            defmodule HeredocTypeDoc do
              @typedoc ""${'"'}
              The result of an operation.
              ""${'"'}
              @type t :: :ok | :error
            end
            """.trimIndent(),
            attributeName = "type"
        )

        val typeDocumentation = assertInstanceOf(docs, FetchedDocs.TypeDocumentation::class.java)

        assertEquals("HeredocTypeDoc", typeDocumentation.module)
        assertEquals("The result of an operation.\n", typeDocumentation.typedoc)
    }

    private fun fetchDocsForModuleAttribute(code: String, attributeName: String): FetchedDocs? {
        myFixture.configureByText(ElixirFileType.INSTANCE, code)

        val moduleAttribute = PsiTreeUtil
            .findChildrenOfType(myFixture.file, AtUnqualifiedNoParenthesesCall::class.java)
            .single { it.atIdentifier.identifierName() == attributeName }

        return SourceFileDocsHelper.fetchDocs(moduleAttribute)
    }
}
