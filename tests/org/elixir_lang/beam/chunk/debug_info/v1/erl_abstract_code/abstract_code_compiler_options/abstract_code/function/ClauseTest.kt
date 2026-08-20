package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.function

import com.intellij.psi.PsiErrorElement
import com.intellij.psi.SyntaxTraverser
import org.elixir_lang.PlatformTestCase

class ClauseTest : PlatformTestCase() {
    fun testInlineFallbackIsParsable() {
        val fallback = "..."

        val file = myFixture.configureByText(
            "hex_pb_package.ex",
            """
            defmodule :hex_pb_package do
              def get_msg_defs(), do: $fallback
            end
            """.trimIndent()
        )

        val hasPsiErrors = SyntaxTraverser.psiTraverser(file)
            .traverse()
            .filter(PsiErrorElement::class.java)
            .isNotEmpty

        assertFalse("Expected no PsiErrorElement for inline fallback", hasPsiErrors)
    }

}
