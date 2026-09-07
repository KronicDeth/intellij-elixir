package org.elixir_lang.code_insight.completion.contributor

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementPresentation
import org.elixir_lang.PlatformTestCase

/**
 * Remote (qualified) completion must offer a function declared only by `defdelegate`.
 *
 * `Delegation.is` and `CallDefinitionClause.is` are disjoint, so the clause filter in
 * `ModuleFunctionLookupElements` drops every delegate. The fixture reproduces the reported contrast:
 * `merge` clears the filter via a sibling `def merge/3`, `values` is only ever a delegate.
 */
class Issue1613RemoteCompletionTest : PlatformTestCase() {
    fun testDelegatedFunctionOfferedInRemoteCompletion() {
        myFixture.configureByFiles("defdelegate_usage.ex", "defdelegate_declaration.ex")
        myFixture.complete(CompletionType.BASIC, 1)

        val lookupElementStrings = myFixture.lookupElementStrings

        assertNotNull("Completion lookup not shown", lookupElementStrings)
        assertTrue(
            "Remote completion should offer values/1, declared only by defdelegate, got: $lookupElementStrings",
            lookupElementStrings!!.contains("values")
        )
        assertTrue(
            "Remote completion should offer merge, got: $lookupElementStrings",
            lookupElementStrings.contains("merge")
        )
    }

    /**
     * The same gap with a `to:` target that does resolve, to source, in the same project.
     *
     * Without this the suite cannot tell the declaration-site filter from a target that simply does
     * not resolve - `to: :maps` above resolves to nothing in the fixture, so on its own it is
     * consistent with either cause. The filter never consults `to:`, so this must fail too.
     */
    fun testDelegatedFunctionWithResolvableSourceTargetOfferedInRemoteCompletion() {
        myFixture.configureByFiles(
            "defdelegate_source_target_usage.ex",
            "defdelegate_source_target_declaration.ex"
        )
        myFixture.complete(CompletionType.BASIC, 1)

        val lookupElementStrings = myFixture.lookupElementStrings

        assertNotNull("Completion lookup not shown", lookupElementStrings)
        assertTrue(
            "Remote completion should offer values/1 delegated to a resolvable source module, got: " +
                "$lookupElementStrings",
            lookupElementStrings!!.contains("values")
        )
    }

    /**
     * The caret with another statement after it, which the suite already treats as a separate case for
     * ordinary clauses (`testQualifiedFunctionOfferedWhenAnotherStatementFollowsInBlock`) because the
     * trailing dot parses differently there.
     */
    fun testDelegatedFunctionOfferedWhenAnotherStatementFollowsInBlock() {
        myFixture.configureByFiles(
            "defdelegate_following_statement_usage.ex",
            "defdelegate_declaration.ex"
        )
        myFixture.complete(CompletionType.BASIC, 1)

        val lookupElementStrings = myFixture.lookupElementStrings

        assertNotNull("Completion lookup not shown", lookupElementStrings)
        assertTrue(
            "Remote completion should offer values/1 with a following statement, got: $lookupElementStrings",
            lookupElementStrings!!.contains("values")
        )
    }

    /**
     * A delegated function must show its signature like any other, or it reads as broken sitting next
     * to entries that have one. The shared delegation renderer now appends the head's parameters;
     * `testIssue2122` pins the same shape for local completion.
     */
    fun testDelegatedFunctionRendersItsHeadSignature() {
        myFixture.configureByFiles("defdelegate_usage.ex", "defdelegate_declaration.ex")
        myFixture.complete(CompletionType.BASIC, 1)

        val values = myFixture.lookupElements.orEmpty().first { lookupElement ->
            LookupElementPresentation().also(lookupElement::renderElement).itemText == "values"
        }
        val presentation = LookupElementPresentation().also(values::renderElement)

        assertEquals("values", presentation.itemText)
        assertTrue(
            "Expected the delegate's parameters in the tail, got: ${presentation.tailText}",
            presentation.tailText.orEmpty().startsWith("(map)")
        )
    }

    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/code_insight/completion/contributor/call_definition_clause"
}
