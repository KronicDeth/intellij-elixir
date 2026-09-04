package org.elixir_lang.psi.impl.declarations

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.psi.impl.ProcessDeclarationsImpl.DECLARING_SCOPE
import org.elixir_lang.psi.operation.And
import org.elixir_lang.psi.operation.Infix
import org.elixir_lang.psi.operation.Match
import org.elixir_lang.psi.operation.Type

/**
 * `&&`, `=` and `::` each declare differently from an ordinary call, so their generated
 * `processDeclarations` must reach the operation-specific implementation rather than the one for
 * any call. Each case asserts on what only that implementation does with the processor: the call
 * fallback hands a `&&`, `=` or `::` to no processor at all.
 */
class OperationDispatchTest : PlatformTestCase() {
    /** Walking up from the right operand, `&&` offers its left operand as a non-declaring scope. */
    fun testAndOffersLeftOperandFromRightOperand() {
        val and = operationIn<And>("{:ok, value} = func() && value == 1")
        val place = and.rightOperand()!!
        val recorder = Recorder()

        and.processDeclarations(recorder, ResolveState.initial(), place, place)

        assertEquals(listOf(and.leftOperand()), recorder.elements)
        assertEquals(listOf(false), recorder.declaringScopes)
    }

    /** Walking up from the right operand, `=` offers only the right operand. */
    fun testMatchOffersRightOperandFromRightOperand() {
        val match = operationIn<Match>("value = compute()")
        val place = match.rightOperand()!!
        val recorder = Recorder()

        match.processDeclarations(recorder, ResolveState.initial(), place, place)

        assertEquals(listOf(place), recorder.elements)
    }

    /** `::` offers its left operand, the type head, whichever side the walk comes from. */
    fun testTypeOffersLeftOperand() {
        val type = operationIn<Type>("@type t :: term")
        val place = type.rightOperand()!!
        val recorder = Recorder()

        type.processDeclarations(recorder, ResolveState.initial(), place, place)

        assertEquals(listOf(type.leftOperand()), recorder.elements)
    }

    private inline fun <reified T : Infix> operationIn(text: String): T {
        val file = myFixture.configureByText("dispatch.ex", text) as ElixirFile
        val operation = PsiTreeUtil.findChildOfType(file, T::class.java)
        assertNotNull("fixture did not parse to ${T::class.simpleName}", operation)

        return operation!!
    }

    private class Recorder : PsiScopeProcessor {
        val elements = mutableListOf<PsiElement>()
        val declaringScopes = mutableListOf<Boolean?>()

        override fun execute(element: PsiElement, state: ResolveState): Boolean {
            elements.add(element)
            declaringScopes.add(state.get(DECLARING_SCOPE))

            return true
        }
    }
}
