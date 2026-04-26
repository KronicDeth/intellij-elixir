package org.elixir_lang.injection.markdown

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.LoggedErrorProcessor
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.intellij.plugins.markdown.lang.MarkdownLanguage

/**
 * Verifies that the markdown [Injector] handles all standard Elixir documentation metadata keys
 * without logging SEVERE errors.
 *
 * The fixture is extracted from Elixir 1.11.3 `kernel.ex` (line ~5131), where `defdelegate`
 * generates `@doc delegate_to: {target, as, :erlang.length(as_args)}`.
 *
 * These tests verify that `delegate_to` (and other non-markdown metadata keys) are correctly
 * skipped by the injector.
 */
class DelegateToInjectorTest : PlatformTestCase() {

    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/injection/markdown"

    /**
     * Verifies that `@doc delegate_to: {...}` does not trigger a SEVERE error in the injector.
     *
     * Before the fix, navigating to a file containing `@doc delegate_to:` would produce:
     * ```
     * SEVERE - #org.elixir_lang.injection.markdown.Injector -
     * Do not known whether to inject Markdown in documentation key delegate_to
     * ```
     */
    fun testDelegateToKeyDoesNotCauseSevereError() {
        myFixture.configureByFile("delegate_to.ex")

        var severeLogged = false
        var severeMessage: String? = null

        val processor = object : LoggedErrorProcessor() {
            override fun processError(
                category: String,
                message: String,
                details: Array<out String>,
                t: Throwable?
            ): Set<Action> {
                val normalizedCategory = category.removePrefix("#")
                if (normalizedCategory.contains("elixir_lang.injection.markdown.Injector")) {
                    severeLogged = true
                    severeMessage = message
                }
                // Return NONE to prevent the error from propagating and failing the test infrastructure
                return Action.NONE
            }
        }

        LoggedErrorProcessor.executeWith<RuntimeException>(processor) {
            // Trigger highlighting — this invokes all MultiHostInjectors including our Injector
            myFixture.doHighlighting()
        }

        assertFalse(
            "The markdown Injector logged a SEVERE error for an @doc keyword pair: $severeMessage. " +
                "The injector must handle 'delegate_to' (and any other non-markdown metadata key) " +
                "without logging errors.",
            severeLogged
        )
    }

    /**
     * Verifies that `@doc delegate_to: {...}` is NOT injected with Markdown.
     *
     * The `delegate_to` value is a tuple `{module, function, arity}`, not markdown prose.
     * Markdown injection would produce nonsensical parse errors.
     */
    fun testDelegateToValueIsNotInjectedWithMarkdown() {
        myFixture.configureByFile("delegate_to.ex")

        val psiFile = myFixture.file
        val injectedLanguageManager = InjectedLanguageManager.getInstance(myFixture.project)

        // Find the @doc delegate_to: element
        val atCalls = PsiTreeUtil.findChildrenOfType(psiFile, AtUnqualifiedNoParenthesesCall::class.java)
        val delegateToDoc = atCalls.find { atCall ->
            val atName = atCall.atIdentifier.lastChild?.text
            atName == "doc" && atCall.text.contains("delegate_to")
        }

        assertNotNull(
            "Could not find @doc delegate_to: in fixture",
            delegateToDoc
        )

        // Walk the delegate_to @doc's subtree and check no child has Markdown injected
        val delegateToElement = delegateToDoc!!
        val injectedInDelegateTo = mutableListOf<String>()

        fun checkInjections(element: com.intellij.psi.PsiElement) {
            val injectedFiles = injectedLanguageManager.getInjectedPsiFiles(element)
            injectedFiles?.forEach { pair ->
                val injectedFile = pair.first
                if (injectedFile.language == MarkdownLanguage.INSTANCE) {
                    injectedInDelegateTo.add(
                        "Markdown injected at '${element.text.take(40)}' (${element.javaClass.simpleName})"
                    )
                }
            }
            element.children.forEach(::checkInjections)
        }
        checkInjections(delegateToElement)

        assertTrue(
            "Markdown was injected into @doc delegate_to: value(s): $injectedInDelegateTo. " +
                "The delegate_to key contains a tuple, not markdown.",
            injectedInDelegateTo.isEmpty()
        )
    }

    /**
     * Verifies that `@doc "..."` heredoc content still gets Markdown injection.
     *
     * This is a sanity check that the fix for `delegate_to` doesn't break normal doc injection.
     */
    fun testNormalDocStringStillGetsMarkdownInjection() {
        myFixture.configureByFile("delegate_to.ex")

        val psiFile = myFixture.file
        val injectedLanguageManager = InjectedLanguageManager.getInstance(myFixture.project)

        // Find the @doc """...""" element (the normal_function's doc)
        val atCalls = PsiTreeUtil.findChildrenOfType(psiFile, AtUnqualifiedNoParenthesesCall::class.java)
        val normalDoc = atCalls.find { atCall ->
            val atName = atCall.atIdentifier.lastChild?.text
            atName == "doc" && atCall.text.contains("A normal docstring")
        }

        assertNotNull(
            "Could not find @doc with normal docstring in fixture",
            normalDoc
        )

        // Check that Markdown IS injected into the heredoc content
        var markdownFound = false

        fun checkInjections(element: com.intellij.psi.PsiElement) {
            val injectedFiles = injectedLanguageManager.getInjectedPsiFiles(element)
            injectedFiles?.forEach { pair ->
                val injectedFile = pair.first
                if (injectedFile.language == MarkdownLanguage.INSTANCE) {
                    markdownFound = true
                }
            }
            element.children.forEach(::checkInjections)
        }
        checkInjections(normalDoc!!)

        assertTrue(
            "Markdown was NOT injected into a normal @doc heredoc. " +
                "The fix must not break standard documentation markdown injection.",
            markdownFound
        )
    }

    /**
     * Verifies that `@doc deprecated: "..."` still gets Markdown injection in its value.
     *
     * The `deprecated` key is the one keyword pair where the value IS markdown.
     */
    fun testDeprecatedKeyStillGetsMarkdownInjection() {
        myFixture.configureByFile("delegate_to.ex")

        val psiFile = myFixture.file
        val injectedLanguageManager = InjectedLanguageManager.getInstance(myFixture.project)

        // Find the @doc deprecated: "..." element
        val atCalls = PsiTreeUtil.findChildrenOfType(psiFile, AtUnqualifiedNoParenthesesCall::class.java)
        val deprecatedDoc = atCalls.find { atCall ->
            val atName = atCall.atIdentifier.lastChild?.text
            atName == "doc" && atCall.text.contains("deprecated")
        }

        assertNotNull(
            "Could not find @doc deprecated: in fixture",
            deprecatedDoc
        )

        var markdownFound = false

        fun checkInjections(element: com.intellij.psi.PsiElement) {
            val injectedFiles = injectedLanguageManager.getInjectedPsiFiles(element)
            injectedFiles?.forEach { pair ->
                val injectedFile = pair.first
                if (injectedFile.language == MarkdownLanguage.INSTANCE) {
                    markdownFound = true
                }
            }
            element.children.forEach(::checkInjections)
        }
        checkInjections(deprecatedDoc!!)

        // Note: deprecated value is a single-line string, which the Injector handles via
        // the ElixirLine branch after recursing into the QuotableKeywordPair value.
        // If this assertion fails, it may mean the deprecated recursion path is broken.
        assertTrue(
            "Markdown was NOT injected into @doc deprecated: value. " +
                "The 'deprecated' key value should still receive markdown injection.",
            markdownFound
        )
    }
}
