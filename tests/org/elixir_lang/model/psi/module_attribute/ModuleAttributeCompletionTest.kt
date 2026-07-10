package org.elixir_lang.model.psi.module_attribute

import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.completeSoleCandidateAtCaret
import org.elixir_lang.code_insight.completionStringsAtCaret

/**
 * Completion for a module attribute reference: `@<prefix><caret>` should offer the in-scope,
 * user-defined module attributes.
 *
 * [org.elixir_lang.model.psi.module_attribute.ModuleAttributeReference] is a Symbol-API
 * [com.intellij.model.psi.PsiSymbolReference] which - unlike a classic `PsiReference` - does not
 * participate in the reference-variants completion path, and there is no `module_attribute` completion
 * contributor nor a `scope/module_attribute/Variants` collector. So nothing is offered yet. This test
 * drives the real completion popup and asserts the correct, user-visible behaviour; it is expected to
 * fail until that functionality is implemented.
 */
@Suppress("UnstableApiUsage")
class ModuleAttributeCompletionTest : PlatformTestCase() {
    fun testModuleAttributeCompletionOffersInScopeAttributes() {
        myFixture.configureByText(
            "test.ex",
            """
                defmodule Test do
                  @request_timeout 5000
                  @request_retries 3

                  def run do
                    @request<caret>
                  end
                end
            """.trimIndent()
        )

        assertEquals(
            listOf("request_retries", "request_timeout"),
            myFixture.completionStringsAtCaret()?.sorted()
        )
    }

    /**
     * When a single in-scope attribute matches the prefix, it is the sole candidate and auto-inserts
     * (a true-prefix lone match completes without opening the popup) - `@time<caret>` becomes
     * `@timeout`. Red until module-attribute completion is implemented (today nothing is offered, so
     * the text is left unchanged at `@time`).
     */
    fun testModuleAttributeCompletionAutoInsertsSoleCandidate() {
        myFixture.configureByText(
            "test.ex",
            """
                defmodule Test do
                  @timeout 5000

                  def run do
                    @time<caret>
                  end
                end
            """.trimIndent()
        )

        myFixture.completeSoleCandidateAtCaret()

        myFixture.checkResult(
            """
                defmodule Test do
                  @timeout 5000

                  def run do
                    @timeout
                  end
                end
            """.trimIndent()
        )
    }
}
