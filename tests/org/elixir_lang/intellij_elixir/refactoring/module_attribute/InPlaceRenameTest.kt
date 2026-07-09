package org.elixir_lang.intellij_elixir.refactoring.module_attribute

import org.elixir_lang.intellij_elixir.refactoring.InPlaceSymbolRenameTestCase

/**
 * Exercises the real IDE Shift+F6 (in-place) rename gesture on a module attribute, from both the
 * declaration and a usage caret. These tests press Shift+F6, type the new name, and press Enter, so
 * they build and commit the inline rename template, exercising the whole in-place path -
 * in particular that [org.elixir_lang.model.psi.module_attribute.ModuleAttributeSymbol]'s pointer
 * survives the template's full-identifier replacement (a plain range marker would collapse and
 * corrupt the asynchronous commit).
 */
class InPlaceRenameTest : InPlaceSymbolRenameTestCase() {
    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/refactoring/module_attribute/rename"
    }

    fun testDeclarationInPlaceRename() {
        myFixture.configureByFiles("declaration.ex")

        inPlaceRenameAtCaret("renamed_attribute")

        myFixture.checkResultByFile("declaration.ex", "declaration_after.ex", false)
    }

    fun testUsageInPlaceRename() {
        myFixture.configureByFiles("usage.ex")

        inPlaceRenameAtCaret("renamed_attribute")

        myFixture.checkResultByFile("usage.ex", "usage_after.ex", false)
    }
}
