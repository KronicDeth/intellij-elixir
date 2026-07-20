package org.elixir_lang.model.psi.variable

import com.intellij.ide.impl.HeadlessDataManager
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.CallDefinitionClause

/**
 * Regression for [VariableSymbol.isDeclaration]: a function-head name (`process` in
 * `def process(data)`) must NOT be classified as a variable declaration, otherwise variable
 * find-usages/navigation could latch onto the definition head.
 */
class VariableHeadDeclarationTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/variable"

    override fun setUp() {
        super.setUp()
        HeadlessDataManager.fallbackToProductionDataManager(myFixture.testRootDisposable)
    }

    fun testFunctionHeadNameIsNotVariableDeclaration() {
        myFixture.configureByFiles("head_declaration.ex")
        val element = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("Element at caret should exist", element)

        // Sanity: the caret really is on the def head name (so the guard path is exercised).
        assertTrue(
            "Caret should be on the def head name",
            CallDefinitionClause.isHead(element!!)
        )
        assertFalse(
            "A function-head name must not be treated as a variable declaration",
            VariableSymbol.isDeclaration(element)
        )
    }
}
