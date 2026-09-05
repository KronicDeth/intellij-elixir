package org.elixir_lang.reference.callable

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
        // suppress = false keeps the default RETHROW, so any logged error fails the test outright;
        // the captured list only sharpens the message for the one this is actually about.
        val (_, loggedErrors) = captureLoggedErrors(suppress = false) {
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

        assertEmpty(
            "Logger.error should not be called for unquoted variable in $displayName",
            loggedErrors.filter {
                it.title?.contains("Don't know how to walk unquoted variable") == true
            }
        )
    }

    /**
     * The unquoted variable is bound to a string literal rather than to a call. A literal has no
     * declarations to walk into, so the walk stops there without comment.
     */
    fun testUnquoteVariableBoundToLiteral() {
        testNoErrorLogged(
            "unquote_variable_bound_to_literal",
            "unquote_variable_bound_to_literal.ex",
            "variable bound to a literal"
        )
    }

    /**
     * The unquoted variable is declared as the value of a keyword parameter, `docs_uri: docs_uri`, so
     * its parent is a keyword pair whose key is not `do`. A parameter declares nothing further up.
     */
    fun testUnquoteVariableFromKeywordParameter() {
        testNoErrorLogged(
            "unquote_variable_from_keyword_parameter",
            "unquote_variable_from_keyword_parameter.ex",
            "variable from a keyword parameter"
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
