package org.elixir_lang.injection

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.SigilHeredoc
import org.elixir_lang.psi.SigilLine
import org.elixir_lang.reference.Callable.Companion.isVariable
import org.elixir_lang.settings.ElixirExperimentalSettings
import org.intellij.lang.regexp.RegExpLanguage

/**
 * Tests for regex sigil injection to ensure interpolation doesn't cause false warnings
 */
class RegexSigilInjectionTest : PlatformTestCase() {
    private var originalEnableHtmlInjection = false

    override fun setUp() {
        super.setUp()
        val settings = ElixirExperimentalSettings.instance
        originalEnableHtmlInjection = settings.state.enableHtmlInjection
        settings.state.enableHtmlInjection = true
        ensureSigilInjectorRegistered()
    }

    override fun tearDown() {
        try {
            ElixirExperimentalSettings.instance.state.enableHtmlInjection = originalEnableHtmlInjection
        } finally {
            super.tearDown()
        }
    }

    /**
     * Test that interpolated regex sigils don't produce "Number expected" warnings
     * when using interpolation with curly braces.
     *
     * The issue: ElixirSigilInjector injects RegExpLanguage across the entire sigil body,
     * including interpolation tokens like #{...}. The regex parser treats { as a quantifier
     * start and expects a number, producing a false "Number expected" warning.
     */
    fun testInterpolatedRegexSigilNoFalseWarning() {
        assertNoProblemHighlights(
            """
                defmodule Test do
                  def test do
                    my_var = %{item: "value"}
                    my_regex = ~r/some text#{my_var.item}somemore text/
                  end
                end
            """.trimIndent()
        )
    }

    /**
     * Test with multiple interpolations in a regex sigil
     */
    fun testMultipleInterpolationsInRegexSigil() {
        assertNoProblemHighlights(
            """
                defmodule Test do
                  def test(params) do
                    regex = ~r|/text_values/#{params.param1}/more_text_values/#{params.param2}|
                  end
                end
            """.trimIndent()
        )
    }

    fun testInterpolatedRegexSigilVariableReferenceResolves() {
        assertNoProblemHighlights(
            """
                defmodule Test do
                  def test do
                    payload_type = "foo"
                    regex = ~r|/items/#{<caret>payload_type}|
                  end
                end
            """.trimIndent()
        )

        val reference = myFixture.file.findReferenceAt(myFixture.caretOffset)
        assertNotNull("Expected reference for interpolated variable", reference)

        val resolved = reference!!.resolve()
        assertNotNull("Expected interpolated variable to resolve", resolved)
        assertTrue("Interpolated variable should be treated as variable", isVariable(resolved!!))
        assertEquals("payload_type", resolved.text)
    }

    fun testInterpolatedRegexHeredocVariableReferenceResolves() {
        assertNoProblemHighlights(
            """
                defmodule Test do
                  def test do
                    payload_type = "foo"
                    regex = ~r'''
                    ^/items/#{<caret>payload_type}$
                    '''
                  end
                end
            """.trimIndent()
        )

        val reference = myFixture.file.findReferenceAt(myFixture.caretOffset)
        assertNotNull("Expected reference for interpolated heredoc variable", reference)

        val resolved = reference!!.resolve()
        assertNotNull("Expected interpolated heredoc variable to resolve", resolved)
        assertTrue("Interpolated heredoc variable should be treated as variable", isVariable(resolved!!))
        assertEquals("payload_type", resolved.text)
    }

    /**
     * Test that literal regex sigils (uppercase ~R) work correctly without interpolation
     */
    fun testLiteralRegexSigilWithBraces() {
        assertNoProblemHighlights(
            """
                defmodule Test do
                  def test do
                    # Literal sigil with actual regex quantifier - should be valid
                    regex = ~R/a{2,3}/
                  end
                end
            """.trimIndent(),
            expectInjection = false
        )
    }

    /**
     * Test interpolation with nested braces
     */
    fun testInterpolatedRegexWithNestedBraces() {
        assertNoProblemHighlights(
            """
                defmodule Test do
                  def test do
                    pattern = "test"
                    regex = ~r/prefix #{pattern <> "{suffix}"} end/
                  end
                end
            """.trimIndent()
        )
    }

    /**
     * Test interpolated regex heredoc sigils
     */
    fun testInterpolatedRegexHeredoc() {
        assertNoProblemHighlights(
            """
                defmodule Test do
                  def test(params) do
                    regex = ~r'''
                    ^/devices/#{params.device_id}$
                    '''
                  end
                end
            """.trimIndent()
        )
    }

    /**
     * Test interpolated regex heredoc sigils with multiple lines
     */
    fun testInterpolatedRegexHeredocMultipleLines() {
        assertNoProblemHighlights(
            """
                defmodule Test do
                  def test(params) do
                    regex = ~r'''
                    ^/events/#{params.event_id}/devices/#{params.device_id}$
                    ^/events/#{params.event_id}/users/#{params.user_id}$
                    '''
                  end
                end
            """.trimIndent()
        )
    }

    /**
     * Test interpolated regex heredoc sigils with multi-line interpolation
     */
    fun testInterpolatedRegexHeredocMultilineInterpolation() {
        assertNoProblemHighlights(
            """
                defmodule Test do
                  def test(params) do
                    regex = ~r'''
                    ^/items/#{
                      params.item_id
                    }$
                    '''
                  end
                end
            """.trimIndent()
        )
    }

    private fun assertNoProblemHighlights(text: String, expectInjection: Boolean = true) {
        val file = myFixture.configureByText("test.ex", text)
        if (expectInjection) {
            assertRegexInjectionPresent(file)
        }
        val problemHighlights = problemHighlights()
        assertTrue(
            "Unexpected highlights: ${formatHighlights(problemHighlights)}",
            problemHighlights.isEmpty()
        )
    }

    private fun assertRegexInjectionPresent(file: PsiFile) {
        val sigilLine = PsiTreeUtil.findChildOfType(file, SigilLine::class.java)
        val sigilHeredoc = PsiTreeUtil.findChildOfType(file, SigilHeredoc::class.java)
        val host = sigilLine ?: sigilHeredoc
        checkNotNull(host) { "Sigil host not found in test file" }

        val injected = InjectedLanguageManager.getInstance(project).getInjectedPsiFiles(host).orEmpty()
        assertTrue(
            "Expected RegExp injection in sigil",
            injected.any { it.first.language.isKindOf(RegExpLanguage.INSTANCE) }
        )
    }

    private fun problemHighlights(): List<HighlightInfo> {
        val minimumSeverity = HighlightSeverity.WEAK_WARNING.myVal
        return myFixture.doHighlighting().filter { info ->
            info.severity.myVal >= minimumSeverity
        }
    }

    private fun formatHighlights(highlights: List<HighlightInfo>): String {
        return highlights.joinToString { info ->
            val description = info.description ?: info.severity.name
            "$description @ ${info.startOffset}-${info.endOffset}"
        }
    }

    private fun ensureSigilInjectorRegistered() {
        InjectedLanguageManager
            .getInstance(project)
            .registerMultiHostInjector(ElixirSigilInjector(), testRootDisposable)
    }

}
