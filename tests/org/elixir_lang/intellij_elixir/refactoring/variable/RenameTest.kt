package org.elixir_lang.intellij_elixir.refactoring.variable

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase

class RenameTest : LightCodeInsightFixtureTestCase() {
    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/refactoring/variable/rename"
    }

    fun testUnused() {
        myFixture.configureByFiles("unused.ex")

        val elementAtCursor = myFixture.file.findReferenceAt(myFixture.caretOffset)!!.element
        myFixture.renameElement(elementAtCursor, "_unused")

        myFixture.checkResultByFile("unused.ex", "unused_after.ex", false)
    }

    fun testUsedDeclaration() {
        myFixture.configureByFiles("used_declaration.ex")

        val elementAtCursor = myFixture.file.findReferenceAt(myFixture.caretOffset)!!.element
        myFixture.renameElement(elementAtCursor, "renamed")

        myFixture.checkResultByFile("used_declaration.ex", "used_after.ex", false)
    }

    fun testUsedUsage() {
        myFixture.configureByFiles("used_usage.ex")

        val resolvedElement = myFixture.file.findReferenceAt(myFixture.caretOffset)!!.resolve()!!
        myFixture.renameElement(resolvedElement, "renamed")

        myFixture.checkResultByFile("used_usage.ex", "used_after.ex", false)
    }
}
