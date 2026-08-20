package org.elixir_lang.documentation

/**
 * Behavioural Quick Documentation (Ctrl+Q) tests for function calls.
 *
 * Drives the real documentation gesture at the caret via [QuickDocumentationTestCase] instead of
 * resolving the call reference in the test, so each case locks the user-visible behaviour ("Ctrl+Q
 * on a documented call shows its `@doc`") independent of how call-site resolution is implemented.
 */
class FunctionCallQuickDocumentationTest : QuickDocumentationTestCase() {
    fun testQuickDocOnLocalFunctionCallShowsAtDoc() {
        myFixture.configureByFiles("local_function.ex")

        val documentation = quickDocumentationAtCaret()

        assertNotNull("Quick Documentation should be shown for a documented local function call", documentation)
        assertTrue(
            "Expected the defining module in the documentation, got: $documentation",
            documentation!!.contains("<b>Documented</b>")
        )
        assertTrue(
            "Expected the function head in the documentation, got: $documentation",
            documentation.contains("add")
        )
        assertTrue(
            "Expected the @doc body in the documentation, got: $documentation",
            documentation.contains("Adds two numbers together")
        )
    }

    fun testQuickDocOnRemoteFunctionCallShowsAtDoc() {
        myFixture.configureByFiles("remote_function.ex")

        val documentation = quickDocumentationAtCaret()

        assertNotNull("Quick Documentation should be shown for a documented remote function call", documentation)
        assertTrue(
            "Expected the defining module in the documentation, got: $documentation",
            documentation!!.contains("<b>Callee</b>")
        )
        assertTrue(
            "Expected the function head in the documentation, got: $documentation",
            documentation.contains("multiply")
        )
        assertTrue(
            "Expected the @doc body in the documentation, got: $documentation",
            documentation.contains("Multiplies two numbers")
        )
    }

    fun testQuickDocOnModuleAliasShowsModuleDoc() {
        myFixture.configureByFiles("module_alias.ex")

        val documentation = quickDocumentationAtCaret()

        assertNotNull("Quick Documentation should be shown for a documented module alias", documentation)
        assertTrue(
            "Expected the module definition in the documentation, got: $documentation",
            documentation!!.contains("<i>module</i> <b>Callee</b>")
        )
        assertTrue(
            "Expected the @moduledoc body in the documentation, got: $documentation",
            documentation.contains("Callee module documentation")
        )
    }

    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/documentation/local_function_quick_doc"
}
