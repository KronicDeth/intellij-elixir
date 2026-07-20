package org.elixir_lang.intellij_elixir.refactoring.type

import org.elixir_lang.intellij_elixir.refactoring.InPlaceSymbolRenameTestCase

/**
 * Exercises the real IDE Shift+F6 (in-place) rename gesture on a type: press Shift+F6, type the new
 * name, press Enter, then assert the whole document renamed correctly. This drives the inline
 * template and its asynchronous commit - the actual user path.
 */
class InPlaceRenameTest : InPlaceSymbolRenameTestCase() {
    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/refactoring/type/rename"
    }

    fun testDeclarationInPlaceRename() {
        myFixture.configureByFiles("declaration.ex")

        inPlaceRenameAtCaret("renamed_type")

        myFixture.checkResultByFile("declaration.ex", "declaration_after.ex", false)
    }

    fun testUsageInPlaceRename() {
        myFixture.configureByFiles("usage.ex")

        inPlaceRenameAtCaret("renamed_type")

        myFixture.checkResultByFile("usage.ex", "usage_after.ex", false)
    }

    fun testPrivateTypeDeclarationInPlaceRename() {
        myFixture.configureByFiles("private_declaration.ex")

        inPlaceRenameAtCaret("renamed_type")

        myFixture.checkResultByFile("private_declaration.ex", "private_declaration_after.ex", false)
    }

    fun testOpaqueTypeDeclarationInPlaceRename() {
        myFixture.configureByFiles("opaque_declaration.ex")

        inPlaceRenameAtCaret("renamed_type")

        myFixture.checkResultByFile("opaque_declaration.ex", "opaque_declaration_after.ex", false)
    }
}
