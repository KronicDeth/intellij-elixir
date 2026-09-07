package org.elixir_lang.code_insight.completion.contributor

import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.completeCandidateAtCaret

/**
 * Accepting a delegated function from completion must insert what accepting an ordinary `def` inserts.
 *
 * [Issue1613RemoteCompletionTest] covers the offer; this covers the insertion, which used to differ
 * because delegation entries carried no insert handler. The MFA-atom case is why the flag is threaded
 * rather than the handler simply attached.
 */
class Issue1613DelegateInsertionTest : PlatformTestCase() {
    fun testAcceptingADelegatedFunctionInsertsParentheses() {
        myFixture.configureByFiles("defdelegate_usage.ex", "defdelegate_declaration.ex")

        val text = myFixture.completeCandidateAtCaret("values")

        assertTrue(
            "Accepting the delegate `values` should insert `values()`, as accepting a `def` does; got:\n$text",
            text.contains("DefdelegateDeclaration.values()")
        )
    }

    /**
     * The sibling `def`-backed name, from the same popup, so a failure above cannot be blamed on the
     * fixture or on the insertion helper.
     */
    fun testAcceptingAClauseBackedFunctionInsertsParentheses() {
        myFixture.configureByFiles("defdelegate_usage.ex", "defdelegate_declaration.ex")

        val text = myFixture.completeCandidateAtCaret("merge")

        assertTrue(
            "Accepting `merge` should insert `merge()`; got:\n$text",
            text.contains("DefdelegateDeclaration.merge()")
        )
    }

    /** `apply(Mod, :values, args)` names the function; appending `()` there would be wrong. */
    fun testAcceptingADelegatedFunctionAsAnMfaAtomInsertsNoParentheses() {
        myFixture.configureByFiles("defdelegate_mfa_atom_usage.ex", "defdelegate_declaration.ex")

        val text = myFixture.completeCandidateAtCaret("values")

        assertFalse(
            "An MFA atom names a function rather than calling it, so `:values` must not gain `()`; got:\n$text",
            text.contains(":values()")
        )
        assertTrue(
            "Expected the bare atom `:values` to be inserted; got:\n$text",
            text.contains(":values")
        )
    }

    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/code_insight/completion/contributor/call_definition_clause"
}
