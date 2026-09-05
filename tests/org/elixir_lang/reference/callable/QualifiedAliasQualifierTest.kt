package org.elixir_lang.reference.callable

import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.reference.Callable

/**
 * An alias must expand to an atom at compile time, so Elixir rejects `x.Foo` with "invalid alias". The identifier
 * qualifying an alias is therefore never a variable, even where a match above would otherwise make it one.
 */
class QualifiedAliasQualifierTest : PlatformTestCase() {
    fun testVariableCannotQualifyAnAlias() = assertNotVariable("y = <caret>x.Foo\n")

    fun testModuleCannotQualifyAnAliasAsAVariable() = assertNotVariable("y = <caret>__MODULE__.Foo\n")

    private fun assertNotVariable(text: String) {
        myFixture.configureByText("qualifier.ex", text)
        val qualifier = PsiTreeUtil.getParentOfType(
            myFixture.file.findElementAt(myFixture.caretOffset),
            UnqualifiedNoArgumentsCall::class.java
        )
        assertNotNull("caret is not on a call", qualifier)

        val (isVariable, errors) = captureLoggedErrors { Callable.isVariable(qualifier!!) }

        assertEmpty(errors)
        assertFalse("an alias qualifier is not a variable", isVariable)
    }
}
