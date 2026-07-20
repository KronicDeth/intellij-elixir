package org.elixir_lang.intellij_elixir.refactoring.type_variable

import org.elixir_lang.intellij_elixir.refactoring.InPlaceSymbolRenameTestCase

/**
 * Exercises the real IDE Shift+F6 (in-place) rename gesture on a type variable declared in a `@type`
 * head or bound by a `@spec ... when` clause. Renaming from either the declaration or a usage should
 * update every occurrence within the enclosing type-spec - and nothing outside it (e.g. the same-named
 * function-clause variables in the accompanying `def`).
 */
class InPlaceRenameTest : InPlaceSymbolRenameTestCase() {
    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/refactoring/type_variable/rename"
    }

    fun testTypeHeadDeclarationInPlaceRename() {
        myFixture.configureByFiles("declaration.ex")

        inPlaceRenameAtCaret("renamed")

        myFixture.checkResultByFile("declaration.ex", "declaration_after.ex", false)
    }

    fun testSpecWhenUsageInPlaceRename() {
        myFixture.configureByFiles("spec_usage.ex")

        inPlaceRenameAtCaret("renamed")

        myFixture.checkResultByFile("spec_usage.ex", "spec_usage_after.ex", false)
    }
}
