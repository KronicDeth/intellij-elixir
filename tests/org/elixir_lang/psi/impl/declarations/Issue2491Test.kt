package org.elixir_lang.psi.impl.declarations

import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.ElixirAnonymousFunction
import org.elixir_lang.psi.ElixirStabOperation
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall

class Issue2491Test : PlatformTestCase() {
    /**
     * `fn p end` is what `fn p -> ... end` looks like part-way through being typed. Without the `->`, the
     * parameter's ancestors hold no [ElixirStabOperation], so the use scope walk used to run past the
     * anonymous function to the file and report reaching file scope as unexpected.
     */
    fun testAnonymousFunctionWithoutStab() {
        val parameter = configureAndFindParameter("anonymous_function_without_stab.ex")
        val anonymousFunction =
                PsiTreeUtil.getParentOfType(parameter, ElixirAnonymousFunction::class.java)!!

        val (useScope, errors) = captureLoggedErrors(suppress = false) { parameter.useScope }

        assertEmpty(errors)
        assertEquals(LocalSearchScope(anonymousFunction), useScope)
    }

    /**
     * The `->` that the case above is missing. Its [ElixirStabOperation] was always recognised, and still
     * takes precedence over the anonymous function around it.
     */
    fun testAnonymousFunctionWithStab() {
        val parameter = configureAndFindParameter("anonymous_function_with_stab.ex")
        val stabOperation = PsiTreeUtil.getParentOfType(parameter, ElixirStabOperation::class.java)!!

        val (useScope, errors) = captureLoggedErrors(suppress = false) { parameter.useScope }

        assertEmpty(errors)
        assertEquals(LocalSearchScope(stabOperation), useScope)
    }

    private fun configureAndFindParameter(path: String): UnqualifiedNoArgumentsCall<*> {
        myFixture.configureByFiles(path)

        val parameter = PsiTreeUtil.getParentOfType(
                myFixture.file.findElementAt(myFixture.caretOffset),
                UnqualifiedNoArgumentsCall::class.java
        )

        assertNotNull(parameter)

        return parameter!!
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/psi/impl/declarations/issue_2491"
}
