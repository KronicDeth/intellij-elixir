package org.elixir_lang.model.psi.variable

import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall

/** Which side of `<-` binds does not depend on a match enclosing the whole generator. */
class VariableSymbolDeclarationTest : PlatformTestCase() {
    fun testGeneratorPatternUnderMatchIsADeclaration() =
        assertDeclaration("result = for <caret>item <- items, do: item\n", true)

    fun testGeneratorEnumerableUnderMatchIsARead() =
        assertDeclaration("result = for item <- <caret>items, do: item\n", false)

    fun testWithArgumentUnderMatchIsARead() =
        assertDeclaration("result = with {:ok, v} <- fetch(<caret>key), do: v\n", false)

    fun testGeneratorEnumerableIsARead() =
        assertDeclaration("for item <- <caret>items, do: item\n", false)

    private fun assertDeclaration(text: String, expected: Boolean) {
        myFixture.configureByText("declaration.ex", text)
        val call = PsiTreeUtil.getParentOfType(
            myFixture.file.findElementAt(myFixture.caretOffset),
            UnqualifiedNoArgumentsCall::class.java
        )
        assertNotNull("caret is not on an identifier", call)

        assertEquals("isDeclaration", expected, VariableSymbol.isDeclaration(call!!))
    }
}
