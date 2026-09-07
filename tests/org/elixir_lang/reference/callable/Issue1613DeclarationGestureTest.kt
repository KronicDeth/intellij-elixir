package org.elixir_lang.reference.callable

import com.intellij.ide.impl.HeadlessDataManager
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.GtduNavigation
import org.elixir_lang.code_insight.gtduNavigationAtCaret

/**
 * Ctrl+Click on the name in a `def` head offers Show Usages, because the name is a declaration.
 *
 * A `defdelegate` head navigates to its target instead - it has two references and no declaration, and
 * the platform prefers a reference. That gap is #4043 and is deliberately not asserted here.
 */
class Issue1613DeclarationGestureTest : PlatformTestCase() {
    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/reference/callable/issue_1613_declaration_gesture"

    override fun setUp() {
        super.setUp()
        HeadlessDataManager.fallbackToProductionDataManager(myFixture.testRootDisposable)
    }

    fun testCtrlClickOnDefNameShowsUsages() {
        myFixture.configureByFile("def_control.ex")

        val navigation = myFixture.gtduNavigationAtCaret()

        assertTrue(
            "Ctrl+Click on a def's own name should offer Show Usages, got: $navigation",
            navigation is GtduNavigation.ShowUsages
        )
    }
}
