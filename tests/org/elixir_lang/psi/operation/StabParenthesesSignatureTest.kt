package org.elixir_lang.psi.operation

import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.psi.ElixirStabParenthesesSignature

/**
 * `(a, b)` and `(a, b) when guard` in front of `->` are the same node. A node whose operator is
 * optional cannot honour [Operation.operator], which promises one, so the signature is not an
 * operation: it is an argument list that may carry a guard.
 */
class StabParenthesesSignatureTest : PlatformTestCase() {
    fun testSignatureWithoutGuardIsNotAnOperation() {
        val signature = signatureIn("fn (a) -> a end")

        assertNull(signature.whenInfixOperator)
        assertFalse("a signature without a guard has no operator to offer", signature is Operation)
    }

    fun testSignatureWithGuardIsNotAnOperation() {
        val signature = signatureIn("fn (a) when is_atom(a) -> a end")

        assertNotNull(signature.whenInfixOperator)
        assertFalse("the guard does not make the signature an operation", signature is Operation)
    }

    private fun signatureIn(text: String): ElixirStabParenthesesSignature {
        val file = myFixture.configureByText("signature.ex", text) as ElixirFile
        val signature = PsiTreeUtil.findChildOfType(file, ElixirStabParenthesesSignature::class.java)
        assertNotNull("fixture did not parse to a parentheses signature", signature)

        return signature!!
    }
}
