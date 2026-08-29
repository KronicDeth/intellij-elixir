package org.elixir_lang.model.psi.variable

import com.intellij.ide.impl.HeadlessDataManager
import com.intellij.psi.PsiElement
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.ElixirVariable
import org.elixir_lang.psi.call.Call

/**
 * Pins [VariableSymbol.isDeclaration], the predicate that decides whether a name binds a variable.
 *
 * "Is a variable" and "is a variable declaration" are separate questions: the first is
 * [VariableSymbol.classify], the second reaches `isVariableDeclaration` and asks whether the element
 * sits in the left operand of the nearest `Match`. The cases here are the shapes where the two
 * answers were unasserted or could plausibly disagree.
 */
class VariableDeclarationTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/variable"

    override fun setUp() {
        super.setUp()
        HeadlessDataManager.fallbackToProductionDataManager(myFixture.testRootDisposable)
    }

    /**
     * A function-head name (`process` in `def process(data)`) must NOT be classified as a variable
     * declaration, otherwise variable find-usages/navigation could latch onto the definition head.
     */
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

    /**
     * `Callable.isVariable` has treated a `var!` name as a variable since 2016, but nothing covered
     * `var!` on the declaration path.
     */
    fun testVarBangNameIsVariableDeclaration() {
        myFixture.configureByFiles("var_bang_declaration.ex")

        val subject = classifiableAtCaret()

        assertEquals("Should have classified the `foo` inside `var!`", "foo", subject.text)
        assertEquals(
            "The name inside `var!` should classify as a variable",
            VariableSymbol.Kind.VARIABLE,
            VariableSymbol.classify(subject)
        )
        assertTrue(
            "`var!(foo) = 1` should declare `foo`",
            VariableSymbol.isDeclaration(subject)
        )
    }

    /**
     * The negative control for the test above: same `var!` name, same classification, but on the
     * RIGHT of the match, where it is a read rather than a binding. Without this, a
     * [VariableSymbol.isDeclaration] that simply returned `true` for anything inside `var!` would
     * satisfy the first test and still be wrong.
     */
    fun testVarBangNameInMatchRightOperandIsNotDeclaration() {
        myFixture.configureByFiles("var_bang_read.ex")

        val subject = classifiableAtCaret()

        assertEquals("Should have classified the `foo` inside `var!`", "foo", subject.text)
        assertEquals(
            "The name inside `var!` should still classify as a variable",
            VariableSymbol.Kind.VARIABLE,
            VariableSymbol.classify(subject)
        )
        assertFalse(
            "`x = var!(foo)` reads `foo`, so it must not be a declaration",
            VariableSymbol.isDeclaration(subject)
        )
    }

    /**
     * The caret sits on an identifier leaf, but [VariableSymbol.classify] only accepts an
     * `UnqualifiedNoArgumentsCall` or an [ElixirVariable] - so walk up to the nearest element it
     * will actually classify, and fail loudly rather than silently asserting against a `null`
     * classification if there is none.
     */
    private fun classifiableAtCaret(): PsiElement {
        val leaf = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("Element at caret should exist", leaf)

        val subject = generateSequence(leaf as PsiElement) { it.parent }
            .firstOrNull { it is ElixirVariable || (it is Call && VariableSymbol.classify(it) != null) }

        assertNotNull(
            "Expected an element at the caret that VariableSymbol can classify",
            subject
        )

        return subject!!
    }
}
