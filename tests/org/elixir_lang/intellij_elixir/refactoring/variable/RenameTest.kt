package org.elixir_lang.intellij_elixir.refactoring.variable

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RenameTest : BasePlatformTestCase() {
    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/refactoring/variable/rename"
    }

    fun testParameterUnused() {
        myFixture.configureByFiles("parameter_unused.ex")

        val elementAtCursor = myFixture.file.findReferenceAt(myFixture.caretOffset)!!.element
        myFixture.renameElement(elementAtCursor, "_unused")

        myFixture.checkResultByFile("parameter_unused.ex", "parameter_unused_after.ex", false)
    }

    fun testParameterUsedDeclaration() {
        myFixture.configureByFiles("parameter_used_declaration.ex")

        val elementAtCursor = myFixture.file.findReferenceAt(myFixture.caretOffset)!!.element
        myFixture.renameElement(elementAtCursor, "renamed")

        myFixture.checkResultByFile("parameter_used_declaration.ex", "parameter_used_after.ex", false)
    }

    fun testParameterUsedUsage() {
        myFixture.configureByFiles("parameter_used_usage.ex")

        val resolvedElement = myFixture.file.findReferenceAt(myFixture.caretOffset)!!.resolve()!!
        myFixture.renameElement(resolvedElement, "renamed")

        myFixture.checkResultByFile("parameter_used_usage.ex", "parameter_used_after.ex", false)
    }

    fun testVariableUnused() {
        myFixture.configureByFiles("variable_unused.ex")

        val elementAtCursor = myFixture.file.findReferenceAt(myFixture.caretOffset)!!.element
        myFixture.renameElement(elementAtCursor, "_unused")

        myFixture.checkResultByFile("variable_unused.ex", "variable_unused_after.ex", false)
    }

    fun testVariableUsedDeclaration() {
        myFixture.configureByFiles("variable_used_declaration.ex")

        val elementAtCursor = myFixture.file.findReferenceAt(myFixture.caretOffset)!!.element
        myFixture.renameElement(elementAtCursor, "renamed")

        myFixture.checkResultByFile("variable_used_declaration.ex", "variable_used_after.ex", false)
    }

    fun testVariableUsedUsage() {
        myFixture.configureByFiles("variable_used_usage.ex")

        val resolvedElement = myFixture.file.findReferenceAt(myFixture.caretOffset)!!.resolve()!!
        myFixture.renameElement(resolvedElement, "renamed")

        myFixture.checkResultByFile("variable_used_usage.ex", "variable_used_after.ex", false)
    }
}
