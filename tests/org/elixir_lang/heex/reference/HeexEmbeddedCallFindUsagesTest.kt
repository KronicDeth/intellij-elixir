package org.elixir_lang.heex.reference

/**
 * Find Usages on a plain Elixir function finds a `{some_function()}` call embedded in a `.heex` file
 * or a `~H` sigil. In a `.heex` file the ordinary `FunctionCallSiteMapper` call walk handles it; in a
 * `~H` sigil the word search lands on the HEEx root, which has no `Call`, so `embeddedCallUsage`
 * re-anchors it in the Elixir root.
 */
class HeexEmbeddedCallFindUsagesTest : HeexHostTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/heex/reference/embedded_call_find_usages"

    fun testPlainCallInHeexFileIsFoundAsUsage() {
        myFixture.configureByFiles("local/page_live.ex", "local/page_live.html.heex")
        assertUsageFound("{some_function()}")
    }

    fun testPlainCallInsideHSigilIsFoundAsUsage() {
        myFixture.configureByText(
            "test.ex",
            heexSigilModuleText(
                entranceModuleText = myFixture.fixtureText("local/page_live.ex"),
                heexBody = myFixture.fixtureText("local/page_live.html.heex")
            )
        )
        assertUsageFound("{some_function()}")
    }

    /**
     * `{delegated_function()}` calls a `defdelegate` in a different module from the real `def`, as
     * `Phoenix.Controller.get_csrf_token/0` delegates to `Plug.CSRFProtection`. The caret is on the
     * real declaration, not the `defdelegate` stub.
     */
    fun testCallThroughDefdelegateInHeexFileIsFoundAsUsage() {
        myFixture.configureByFiles("delegate/helper.ex", "delegate/page_live.ex", "delegate/page_live.html.heex")
        assertUsageFound("{delegated_function()}")
    }

    /** The sigil goes into the `defdelegate` stub's module, since an unqualified call resolves through it only from there. */
    fun testCallThroughDefdelegateInsideHSigilIsFoundAsUsage() {
        myFixture.configureByText(
            "test.ex",
            heexSigilModuleText(
                entranceModuleText = myFixture.fixtureText("delegate/page_live.ex"),
                heexBody = myFixture.fixtureText("delegate/page_live.html.heex"),
                myFixture.fixtureText("delegate/helper.ex")
            )
        )
        assertUsageFound("{delegated_function()}")
    }
}
