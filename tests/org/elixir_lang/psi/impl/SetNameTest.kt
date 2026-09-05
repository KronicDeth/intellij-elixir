package org.elixir_lang.psi.impl

import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.call.Named
import org.elixir_lang.psi.operation.Addition

/**
 * A rename that cannot be carried out must say so, as the platform setName contract requires,
 * rather than report an error and leave the name unchanged. An operator is named by its operator
 * token, which is not an identifier the rename knows how to replace.
 */
class SetNameTest : PlatformTestCase() {
    fun testRenamingAnOperatorReportsItCannotRename() {
        myFixture.configureByText("operation.ex", "a + b\n")
        val addition = PsiTreeUtil.findChildOfType(myFixture.file, Addition::class.java) as Named

        val (thrown, errors) = captureLoggedErrors {
            try {
                addition.setName("x")
                null
            } catch (incorrectOperation: IncorrectOperationException) {
                incorrectOperation
            }
        }

        assertEmpty("a refused rename is reported through the exception, not the log", errors)
        assertNotNull("renaming an operator must be refused", thrown)
    }
}
