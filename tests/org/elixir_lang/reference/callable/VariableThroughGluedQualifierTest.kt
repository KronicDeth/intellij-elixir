package org.elixir_lang.reference.callable

import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.reference.Callable

/**
 * `Qualifier.` typed above a line starting with a tuple pattern parses as one qualified multiple-alias node. The
 * variable resolver declares through it deliberately, so the walks that say what a variable is and where it is used
 * must look through it too.
 */
class VariableThroughGluedQualifierTest : PlatformTestCase() {
    fun testVariableInsideGluedQualifierIsAVariableWithAScope() {
        myFixture.configureByText("glued.ex", "Qualifier.\n{:ok, <caret>value} = call()\n")
        val variable = PsiTreeUtil.getParentOfType(
            myFixture.file.findElementAt(myFixture.caretOffset),
            UnqualifiedNoArgumentsCall::class.java
        )
        assertNotNull("caret is not on a variable", variable)

        val (isVariable, errors) = captureLoggedErrors { Callable.isVariable(variable!!) }

        assertEmpty(errors)
        assertTrue("value is not a variable", isVariable)
        assertEquals(
            listOf("Qualifier.\n{:ok, value} = call()"),
            (Callable.variableUseScope(variable!!) as LocalSearchScope).scope.map { it.text }
        )
    }
}
