package org.elixir_lang.injection.markdown

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.ElixirTypes
import org.elixir_lang.psi.HeredocLiteral
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

        val (_, loggedErrors) = captureLoggedErrors {
            // Trigger highlighting - this invokes all MultiHostInjectors including our Injector
            myFixture.doHighlighting()
        }

        val injectorErrors = loggedErrors.filter {
            it.category.contains("elixir_lang.injection.markdown.Injector")
        }

        assertEmpty(
            "The markdown Injector logged a SEVERE error for an @doc keyword pair: " +
                "${injectorErrors.map { it.message }}. " +
                "The injector must handle 'delegate_to' (and any other non-markdown metadata key) " +
                "without logging errors.",
            injectorErrors
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

    /**
     * ExDoc lets `@doc` carry any metadata key, and a doc value can be any expression. Neither an
     * unlisted key such as `tags:` nor a `<>` concatenation is prose to inject Markdown into, and
     * neither is an error: the injector names what it injects and passes over the rest.
     */
    fun testUnlistedMetadataKeyAndValueShapeAreNotReported() {
        myFixture.configureByFile("unlisted_metadata.ex")

        val (_, loggedErrors) = captureLoggedErrors {
            myFixture.doHighlighting()
        }

        assertEmpty(
            "The markdown Injector must pass over documentation it does not inject into",
            loggedErrors.filter { it.category.contains("elixir_lang.injection.markdown.Injector") }
        )

        val tagsDoc = PsiTreeUtil.findChildrenOfType(myFixture.file, AtUnqualifiedNoParenthesesCall::class.java)
            .single { it.text.contains("tags:") }
        val injectedLanguageManager = InjectedLanguageManager.getInstance(myFixture.project)
        val markdownInjected = PsiTreeUtil.collectElements(tagsDoc) { true }
            .flatMap { element -> injectedLanguageManager.getInjectedPsiFiles(element).orEmpty() }
            .filter { it.first.language == MarkdownLanguage.INSTANCE }

        assertEmpty("`tags:` is metadata, not Markdown prose", markdownInjected)
    }

    fun testCodeBlockTrailingEndIsInjectedWithElixir() {
        myFixture.configureByFile("trailing_end_code_block.ex")

        val heredoc = PsiTreeUtil.findChildOfType(myFixture.file, HeredocLiteral::class.java)
        assertNotNull("Could not find documentation heredoc in fixture", heredoc)

        val injectedLanguageManager = InjectedLanguageManager.getInstance(myFixture.project)
        val injectedFiles = injectedLanguageManager.getInjectedPsiFiles(heredoc!!).orEmpty()

        val elixirInjectedFile = injectedFiles
            .map { pair -> pair.first }
            .firstOrNull { injectedFile -> injectedFile.language.isKindOf(ElixirLanguage) }

        assertNotNull("Expected Elixir injection for markdown code block", elixirInjectedFile)

        val injectedText = elixirInjectedFile!!.text
        val trailingEndOffset = injectedText.lastIndexOf("end")

        assertTrue(
            "Expected trailing 'end' in injected Elixir text, but got: '$injectedText'",
            trailingEndOffset >= 0
        )

        val trailingEndElement = elixirInjectedFile.findElementAt(trailingEndOffset)
        assertNotNull("Could not resolve PSI element for trailing 'end'", trailingEndElement)
        assertEquals("end", trailingEndElement!!.text)
        assertEquals(
            "Trailing 'end' should be tokenized as Elixir keyword END",
            ElixirTypes.END,
            trailingEndElement.node.elementType
        )
    }
}
