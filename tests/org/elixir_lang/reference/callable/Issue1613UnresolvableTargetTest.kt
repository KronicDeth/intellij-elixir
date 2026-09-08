package org.elixir_lang.reference.callable

import com.intellij.ide.impl.HeadlessDataManager
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.gotoDeclarationTargetsAtCaret

/**
 * Go To Declaration on a call to a delegated function whose `to:` module does not resolve.
 *
 * The head is then the only result, and the symbol layer dropped it for not being a call definition
 * clause, so the gesture reached nothing. Not only a broken-program case - a module delegating to a
 * dependency not yet on the path resolves this way.
 */
class Issue1613UnresolvableTargetTest : PlatformTestCase() {
    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/reference/callable/issue_1613_unresolvable_target"

    override fun setUp() {
        super.setUp()
        HeadlessDataManager.fallbackToProductionDataManager(myFixture.testRootDisposable)
    }

    fun testUsageOfDefdelegateWithUnresolvableTargetNavigatesToTheHead() {
        myFixture.configureByFile("unresolvable_target.ex")

        val declarations = myFixture.gotoDeclarationTargetsAtCaret().orEmpty().mapNotNull { it.destination }

        assertTrue(
            "Go To Declaration should fall back to the defdelegate head when to: does not resolve, got none",
            declarations.isNotEmpty()
        )
    }
}
