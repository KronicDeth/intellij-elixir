package org.elixir_lang.reference.callable

import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.reference.Callable

/**
 * A match inside `#{}` binds its variable for the code after the string, as a match anywhere else in an expression
 * does, so both variable walks carry through the interpolation and the string around it. A bare identifier inside `#{}`
 * is then a variable exactly when the same identifier outside the string would be.
 */
class VariableUseScopeThroughInterpolationTest : PlatformTestCase() {
    fun testDeclarationInsideStringLine() = assertScopeReachesLaterUse("_ = \"#{<caret>x = 1}\"\nx\n")

    fun testDeclarationInsideCharListLine() = assertScopeReachesLaterUse("_ = '#{<caret>x = 1}'\nx\n")

    fun testDeclarationInsideStringHeredoc() = assertScopeReachesLaterUse("_ = \"\"\"\n#{<caret>x = 1}\n\"\"\"\nx\n")

    fun testDeclarationInsideCharListHeredoc() = assertScopeReachesLaterUse("_ = '''\n#{<caret>x = 1}\n'''\nx\n")

    fun testDeclarationInsideSigilLine() = assertScopeReachesLaterUse("_ = ~s(#{<caret>x = 1})\nx\n")

    fun testDeclarationInsideSigilHeredoc() = assertScopeReachesLaterUse("_ = ~s\"\"\"\n#{<caret>x = 1}\n\"\"\"\nx\n")

    fun testDeclarationInsideStringInCallArguments() =
        assertScopeReachesLaterUse("def f do\n  IO.puts(\"#{<caret>x = 1}\")\n  x\nend\n")

    fun testUseInsideStringArgumentIsNotAVariableByFiat() {
        val name = variableAtCaret("def f do\n  IO.puts(\"#{<caret>name}\")\nend\n")

        assertFalse("name is a variable although nothing above the string declares it", Callable.isVariable(name))
    }

    fun testUseInsideStringUnderMatchIsAVariable() {
        val name = variableAtCaret("def f do\n  y = \"#{<caret>name}\"\nend\n")

        assertTrue("name is not a variable although it sits under a match", Callable.isVariable(name))
    }

    private fun assertScopeReachesLaterUse(text: String) {
        val declaration = variableAtCaret(text)
        assertTrue("x is not a variable", Callable.isVariable(declaration))

        val laterUse = PsiTreeUtil
            .collectElementsOfType(myFixture.file, UnqualifiedNoArgumentsCall::class.java)
            .last { it.name == "x" }
        assertNotSame("no use of x after the declaration", declaration, laterUse)

        val (useScope, errors) = captureLoggedErrors { Callable.variableUseScope(declaration) }

        assertEmpty(errors)
        assertNotSame(LocalSearchScope.EMPTY, useScope)
        assertTrue("use scope does not reach the later x", useScope.containsRange(myFixture.file, laterUse.textRange))
    }

    private fun variableAtCaret(text: String): UnqualifiedNoArgumentsCall<*> {
        myFixture.configureByText("interpolation.ex", text)
        val variable = PsiTreeUtil.getParentOfType(
            myFixture.file.findElementAt(myFixture.caretOffset),
            UnqualifiedNoArgumentsCall::class.java
        )
        assertNotNull("caret is not on a variable", variable)

        return variable!!
    }
}
