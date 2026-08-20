package org.elixir_lang.breadcrumbs

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.breadcrumbs.BreadcrumbsUtil
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.call.Call

/**
 * Sticky Lines (the declarations pinned to the top of the editor while scrolling) are derived by the platform
 * from the registered [com.intellij.ui.breadcrumbs.BreadcrumbsProvider]: it walks the PSI ancestry of the caret
 * line and pins every element for which `acceptStickyElement` returns `true`. These tests exercise that provider
 * directly for the Elixir structural declarations (module / protocol / implementation / function / macro).
 *
 * `@Suppress("UnstableApiUsage")` because the tests call `acceptStickyElement`, the sticky-lines hook the
 * platform still marks `@ApiStatus.Experimental`.
 */
@Suppress("UnstableApiUsage")
class ProviderTest : PlatformTestCase() {
    fun testRegisteredForElixirLanguage() {
        // A registered provider is the only thing that enables Sticky Lines for a language.
        val provider = BreadcrumbsUtil.getInfoProvider(ElixirLanguage)

        assertNotNull("A BreadcrumbsProvider must be registered for Elixir to enable Sticky Lines", provider)
        assertInstanceOf(provider, Provider::class.java)
    }

    fun testAcceptsModuleAndFunctionDeclarations() {
        configure()
        val provider = Provider()

        val defmodule = call("defmodule")
        val def = call("def hello")

        assertTrue("defmodule should be a breadcrumb", provider.acceptElement(defmodule))
        assertTrue("def should be a breadcrumb", provider.acceptElement(def))
        assertTrue("defmodule should be a sticky line", provider.acceptStickyElement(defmodule))
        assertTrue("def should be a sticky line", provider.acceptStickyElement(def))
    }

    fun testAcceptsProtocolAndImplementationDeclarations() {
        myFixture.configureByText(
            "protocol.ex",
            """
            defprotocol MyProtocol do
              def frobnicate(data)
            end

            defimpl MyProtocol, for: Integer do
              def frobnicate(data), do: data
            end
            """.trimIndent()
        )
        val provider = Provider()

        assertTrue("defprotocol should be a sticky line", provider.acceptStickyElement(call("defprotocol")))
        assertTrue("defimpl should be a sticky line", provider.acceptStickyElement(call("defimpl")))
    }

    fun testRejectsNonStructuralElements() {
        configure()
        val provider = Provider()

        // An ordinary call site inside the function body is not a declaration.
        assertFalse("call sites should not be sticky", provider.acceptStickyElement(call("IO.puts")))

        // Leaf tokens (whitespace, identifiers, operators) are never declarations.
        val leaf = leafInBody()
        assertFalse("a plain leaf token should not be a breadcrumb", provider.acceptElement(leaf))
        assertFalse("a plain leaf token should not be a sticky line", provider.acceptStickyElement(leaf))
    }

    fun testAcceptsExUnitDescribeAndTestBlocks() {
        configureTest()
        val provider = Provider()

        assertTrue("describe should be a sticky line", provider.acceptStickyElement(call("describe")))
        assertTrue("test should be a sticky line", provider.acceptStickyElement(call("test \"adds\"")))
    }

    fun testElementInfoForExUnitBlocks() {
        configureTest()
        val provider = Provider()

        assertEquals("describe \"arithmetic\"", provider.getElementInfo(call("describe")))
        assertEquals("test \"adds\"", provider.getElementInfo(call("test \"adds\"")))
    }

    fun testStickyAncestryInTestFile() {
        myFixture.configureByText(
            "math_test.exs",
            """
            defmodule MathTest do
              use ExUnit.Case

              describe "arithmetic" do
                test "adds" do
                  assert 1 + <caret>1 == 2
                end
              end
            end
            """.trimIndent()
        )
        val provider = Provider()

        val leaf = myFixture.file.findElementAt(myFixture.caretOffset)!!
        val stickyInfos = generateSequence(leaf) { if (it is PsiFile) null else provider.getParent(it) }
            .filter { provider.acceptStickyElement(it) }
            .map { provider.getElementInfo(it) }
            .toList()

        assertEquals(listOf("test \"adds\"", "describe \"arithmetic\"", "MathTest"), stickyInfos)
    }

    fun testElementInfoUsesElixirNaming() {
        configure()
        val provider = Provider()

        assertEquals("MyModule", provider.getElementInfo(call("defmodule")))
        assertEquals("hello/1", provider.getElementInfo(call("def hello")))
    }

    fun testStickyAncestryFromCaret() {
        configure()
        val provider = Provider()

        val leaf = leafInBody()
        val stickyInfos = generateSequence(leaf) { if (it is PsiFile) null else provider.getParent(it) }
            .filter { provider.acceptStickyElement(it) }
            .map { provider.getElementInfo(it) }
            .toList()

        // Innermost declaration first, matching how the platform stacks sticky lines from the caret outward.
        assertEquals(listOf("hello/1", "MyModule"), stickyInfos)
    }

    private fun configure() {
        myFixture.configureByText(
            "sticky.ex",
            """
            defmodule MyModule do
              def hello(name) do
                IO.puts(name)
              end
            end
            """.trimIndent()
        )
    }

    private fun configureTest() {
        myFixture.configureByText(
            "math_test.exs",
            """
            defmodule MathTest do
              use ExUnit.Case

              describe "arithmetic" do
                test "adds" do
                  assert 1 + 1 == 2
                end
              end
            end
            """.trimIndent()
        )
    }

    private fun call(textPrefix: String): Call =
            PsiTreeUtil.findChildrenOfType(myFixture.file, Call::class.java)
                .first { it.text.startsWith(textPrefix) }

    private fun leafInBody(): PsiElement =
            PsiTreeUtil.collectElements(myFixture.file) { it.firstChild == null && it.text == "name" }
                .last()
}
