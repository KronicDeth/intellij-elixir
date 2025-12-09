package org.elixir_lang.reference.callable

import com.intellij.testFramework.LoggedErrorProcessor
import org.elixir_lang.PlatformTestCase
import java.io.File

/**
 * Regression tests for handling PSI parent elements when walking unquoted variables that match @callback names.
 *
 * Bug pattern discovered: When a variable name matches a @callback name (e.g., docs_uri)
 * and appears in unquote() alongside another unquote(), the reference resolver attempts
 * to resolve the callback-named variable, triggering treeWalkUpUnquotedVariable(). This
 * walks up the PSI tree through parent elements, which must include ElixirStabBody and
 * other transparent wrapper types (Call, ElixirStab, QuotableArguments, QuotableKeywordList).
 *
 * Original issue: Credo's check.ex file
 * See: https://github.com/rrrene/credo/blob/9ba02a636f0ef22b0ad965b2c710c727d1a73902/lib/credo/check.ex
 */
class UnquoteCallbackNamedVariableTest : PlatformTestCase() {

    private fun testNoErrorLogged(fixtureDir: String, fixtureFile: String, displayName: String) {
        var errorLogged = false
        LoggedErrorProcessor.executeWith<RuntimeException>(object : LoggedErrorProcessor() {
            override fun processError(
                category: String,
                message: String,
                details: Array<out String>,
                t: Throwable?
            ): Set<Action> {
                if (t?.message?.contains("Don't know how to walk unquoted variable parent") == true) {
                    errorLogged = true
                }
                return Action.ALL
            }
        }) {
            val testFile = File("testData/org/elixir_lang/reference/callable/$fixtureDir", fixtureFile)
            val content = testFile.readText()
            val psiFile = myFixture.configureByText(fixtureFile, content)

            psiFile.accept(object : com.intellij.psi.PsiRecursiveElementVisitor() {
                override fun visitElement(element: com.intellij.psi.PsiElement) {
                    super.visitElement(element)
                    if (element is com.intellij.psi.PsiNamedElement) {
                        element.references.forEach { ref ->
                            try {
                                ref.resolve()
                            } catch (_: Exception) {
                                // Ignore resolution errors
                            }
                        }
                    }
                }
            })
        }

        assertFalse(
            "Logger.error should not be called for unquoted variable parent in $displayName",
            errorLogged
        )
    }

    /**
     * Tests basic case: unquote of callback-named variable in a quote block within macro.
     * Tests that ElixirStabBody is handled as a transparent wrapper.
     */
    fun testUnquoteInStabBody() {
        testNoErrorLogged(
            "unquote_in_stab_body",
            "unquote_in_stab_body.ex",
            "stab body"
        )
    }

    /**
     * Tests unquote of callback-named variable inside a quote block within a case statement.
     * Tests that ElixirStab and ElixirStabBody are handled as transparent wrappers.
     */
    fun testUnquoteInCaseStabBody() {
        testNoErrorLogged(
            "unquote_in_case_stab_body",
            "unquote_in_case_stab_body.ex",
            "case stab body"
        )
    }

    /**
     * Tests unquote of callback-named variable inside a quote block within an anonymous function.
     * Tests that Call (anonymous function) and ElixirStabBody are handled as transparent wrappers.
     */
    fun testUnquoteInAnonymousFunStabBody() {
        testNoErrorLogged(
            "unquote_in_anonymous_fun_stab_body",
            "unquote_in_anonymous_fun_stab_body.ex",
            "anonymous function stab body"
        )
    }

    /**
     * Tests unquote of callback-named variable alongside bracket operation access.
     * Tests that ElixirStabBody and bracket operations work together correctly.
     */
    fun testUnquoteInBracketOperation() {
        testNoErrorLogged(
            "unquote_in_bracket_operation",
            "unquote_in_bracket_operation.ex",
            "bracket operation"
        )
    }
}
