package org.elixir_lang.intellij_elixir.refactoring.callback

import org.elixir_lang.intellij_elixir.refactoring.InPlaceSymbolRenameTestCase

/**
 * Exercises the real IDE Shift+F6 (in-place) rename gesture on a behaviour callback: press Shift+F6,
 * type the new name, press Enter, then assert the whole document renamed correctly. This drives the
 * inline template and its asynchronous commit - the actual user path.
 */
class InPlaceRenameTest : InPlaceSymbolRenameTestCase() {
    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/refactoring/callback/rename"
    }

    fun testDeclarationInPlaceRename() {
        myFixture.configureByFiles("declaration.ex")

        inPlaceRenameAtCaret("execute")

        myFixture.checkResultByFile("declaration.ex", "declaration_after.ex", false)
    }

    fun testUsageInPlaceRename() {
        myFixture.configureByFiles("usage.ex")

        inPlaceRenameAtCaret("execute")

        myFixture.checkResultByFile("usage.ex", "usage_after.ex", false)
    }

    fun testDefaultImplementationInPlaceRename() {
        myFixture.configureByFiles("default_implementation.ex")

        inPlaceRenameAtCaret("execute")

        myFixture.checkResultByFile("default_implementation.ex", "overridable_after.ex", false)
    }

    fun testDefoverridableInPlaceRename() {
        myFixture.configureByFiles("defoverridable.ex")

        inPlaceRenameAtCaret("execute")

        myFixture.checkResultByFile("defoverridable.ex", "overridable_after.ex", false)
    }

    fun testOverrideInPlaceRename() {
        myFixture.configureByFiles("override.ex")

        inPlaceRenameAtCaret("execute")

        myFixture.checkResultByFile("override.ex", "overridable_after.ex", false)
    }
}
