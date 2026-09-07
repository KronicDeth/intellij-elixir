package org.elixir_lang.documentation

/**
 * Quick Documentation (Ctrl+Q) through a `defdelegate` whose `to:` target is Elixir **source**.
 *
 * The control for [Issue1613BeamDelegateQuickDocumentationTest]. This case works because resolution
 * chases `to:` first and hands the pipeline the target's own `def`, so the missing `Delegation`
 * branch is never reached - which is why the gap went unnoticed.
 */
class Issue1613QuickDocumentationTest : QuickDocumentationTestCase() {
    fun testQuickDocOnCallDelegatedToSourceShowsTargetDoc() {
        myFixture.configureByFiles("delegated_call.ex")

        val documentation = quickDocumentationAtCaret()

        assertNotNull("Quick Documentation should be shown for a call to a delegated function", documentation)
        assertTrue(
            "Expected the delegation target's function head, got: $documentation",
            documentation!!.contains("merge(map1, map2)")
        )
        assertTrue(
            "Expected the delegation target's @doc, got: $documentation",
            documentation.contains("Merges two maps.")
        )
    }

    fun testQuickDocOnDefdelegateDeclarationShowsTargetDoc() {
        myFixture.configureByFiles("delegate_declaration.ex")

        val documentation = quickDocumentationAtCaret()

        assertNotNull("Quick Documentation should be shown for a defdelegate declaration", documentation)
        assertTrue(
            "Expected the delegation target's function head, got: $documentation",
            documentation!!.contains("merge(map1, map2)")
        )
        assertTrue(
            "Expected the delegation target's @doc, got: $documentation",
            documentation.contains("Merges two maps.")
        )
    }

    /**
     * A `defdelegate` may carry its own `@doc`, and when it does that is the more specific answer - the
     * delegating module is saying what the function means *here*, which is the reason to write it.
     * Chasing `to:` regardless would discard it.
     */
    fun testQuickDocPrefersTheDelegatesOwnDocOverTheTargets() {
        myFixture.configureByFiles("documented_delegate.ex")

        val documentation = quickDocumentationAtCaret()

        assertNotNull("Quick Documentation should be shown for a documented delegate", documentation)
        assertTrue(
            "Expected the delegate's own @doc, got: $documentation",
            documentation!!.contains("Delegator's own account of merging.")
        )
        assertFalse(
            "Expected the delegate's own @doc to replace the target's, got: $documentation",
            documentation.contains("Merges two maps.")
        )
    }

    /**
     * A documented `defdelegate` must not lend its `@doc` to a later, undocumented one.
     *
     * The attribute walk collecting `@doc` stops at the previous call definition clause, and a
     * `defdelegate` is not one - so without a delegation boundary it runs straight past any number of
     * intervening delegations and attaches the wrong module attribute.
     */
    fun testDelegateDocDoesNotLeakToALaterDelegate() {
        myFixture.configureByFiles("doc_does_not_leak.ex")

        val documentation = quickDocumentationAtCaret()

        assertFalse(
            "An undocumented delegate must not show the previous delegate's @doc, got: $documentation",
            documentation.orEmpty().contains("Delegator's own account of merging.")
        )
    }

    /** The same boundary in the other direction: a `def` must not inherit a preceding delegate's `@doc`. */
    fun testDelegateDocDoesNotLeakToALaterDef() {
        myFixture.configureByFiles("doc_does_not_leak_into_def.ex")

        val documentation = quickDocumentationAtCaret()

        assertFalse(
            "A def must not show a preceding delegate's @doc, got: $documentation",
            documentation.orEmpty().contains("Delegator's own account of merging.")
        )
    }

    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/documentation/defdelegate_quick_doc"
}
