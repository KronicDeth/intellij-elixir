package org.elixir_lang.documentation

import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.beam.BeamLibraryTestCase
import org.elixir_lang.beam.psi.impl.ModuleImpl
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall

/**
 * Documentation lookup is a positive filter: the elements that carry docs are named, and anything
 * else simply has none. Neither a `@spec` asked for its doc owner nor a decompiled `@type` asked for
 * its docs is an error; each is an ordinary element without documentation.
 */
class UnhandledDocOwnerTest : BeamLibraryTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/type"

    fun testNonDocumentationAttributeHasNoOwner() {
        myFixture.configureByText(
            "spec.ex",
            """
            defmodule Spec do
              @spec f() :: term
              def f, do: 1
            end
            """.trimIndent()
        )
        val spec = PsiTreeUtil.findChildOfType(myFixture.file, AtUnqualifiedNoParenthesesCall::class.java)
        assertNotNull("fixture did not parse to a module attribute", spec)

        val (owner, errors) = captureLoggedErrors {
            ElixirDocumentationProvider().findDocComment(myFixture.file, spec!!.textRange)!!.owner
        }

        assertEmpty("a non-documentation attribute is not an error", errors)
        assertNull("a `@spec` documents nothing", owner)
    }

    fun testDecompiledTypeHasNoDocs() {
        openBeam("queue.beam")
        val module = myFixture.file.children.single() as ModuleImpl<*>
        val type = module.typeDefinitions().firstOrNull()
        assertNotNull("queue.beam decompiled without a type definition", type)

        val (doc, errors) = captureLoggedErrors {
            ElixirDocumentationProvider().generateDoc(type!!, null)
        }

        assertEmpty("an element without docs is not an error", errors)
        assertNull("types only exist for builtins, so there are no docs", doc)
    }
}
