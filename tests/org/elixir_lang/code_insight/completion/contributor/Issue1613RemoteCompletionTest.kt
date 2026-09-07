package org.elixir_lang.code_insight.completion.contributor

import com.intellij.codeInsight.completion.CompletionType
import org.elixir_lang.PlatformTestCase

/**
 * Remote (qualified) completion must offer a function declared only by `defdelegate`.
 *
 * `Delegation.is` and `CallDefinitionClause.is` are disjoint predicates - `defdelegate` is not one of
 * the heads `isFunction`/`isMacro`/`isGuard` test - so the `CallDefinitionClause.is` filter in
 * `ModuleFunctionLookupElements` drops every delegate. The fixture reproduces the reported contrast:
 * `merge` is offered anyway because a sibling `def merge/3` clears the filter under the same name,
 * while `values`, declared only as a delegate, is offered by nothing.
 *
 * The `BeamModule` branch has no such filter, but source modulars are preferred over BEAM-decompiled
 * stubs, so for any module whose source is on disk the unfiltered branch is never the one taken.
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

    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/code_insight/completion/contributor/call_definition_clause"
}
