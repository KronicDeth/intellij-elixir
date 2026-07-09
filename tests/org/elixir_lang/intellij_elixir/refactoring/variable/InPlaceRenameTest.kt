package org.elixir_lang.intellij_elixir.refactoring.variable

import org.elixir_lang.intellij_elixir.refactoring.InPlaceSymbolRenameTestCase

/**
 * Exercises the real IDE Shift+F6 (in-place) rename gesture on local variables and parameters:
 * press Shift+F6, type the new name, press Enter, then assert the whole document renamed correctly.
 * This drives the inline template and its asynchronous commit - the actual user path.
 */
class InPlaceRenameTest : InPlaceSymbolRenameTestCase() {
    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/refactoring/variable/rename"
    }

    fun testParameterUnusedInPlaceRename() {
        myFixture.configureByFiles("parameter_unused.ex")

        inPlaceRenameAtCaret("_unused")

        myFixture.checkResultByFile("parameter_unused.ex", "parameter_unused_after.ex", false)
    }

    fun testParameterUsedDeclarationInPlaceRename() {
        myFixture.configureByFiles("parameter_used_declaration.ex")

        inPlaceRenameAtCaret("renamed")

        myFixture.checkResultByFile("parameter_used_declaration.ex", "parameter_used_after.ex", false)
    }

    fun testParameterUsedUsageInPlaceRename() {
        myFixture.configureByFiles("parameter_used_usage.ex")

        inPlaceRenameAtCaret("renamed")

        myFixture.checkResultByFile("parameter_used_usage.ex", "parameter_used_after.ex", false)
    }

    fun testVariableUnusedInPlaceRename() {
        myFixture.configureByFiles("variable_unused.ex")

        inPlaceRenameAtCaret("_unused")

        myFixture.checkResultByFile("variable_unused.ex", "variable_unused_after.ex", false)
    }

    fun testVariableUsedDeclarationInPlaceRename() {
        myFixture.configureByFiles("variable_used_declaration.ex")

        inPlaceRenameAtCaret("renamed")

        myFixture.checkResultByFile("variable_used_declaration.ex", "variable_used_after.ex", false)
    }

    fun testVariableUsedUsageInPlaceRename() {
        myFixture.configureByFiles("variable_used_usage.ex")

        inPlaceRenameAtCaret("renamed")

        myFixture.checkResultByFile("variable_used_usage.ex", "variable_used_after.ex", false)
    }
}
