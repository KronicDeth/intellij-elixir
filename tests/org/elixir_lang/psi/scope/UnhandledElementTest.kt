package org.elixir_lang.psi.scope

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.ResolveState
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.beam.psi.TypeDefinition as BeamTypeDefinition
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.ElixirDoBlock
import org.elixir_lang.psi.ElixirFile

/**
 * A scope processor is a positive filter: it names the elements it declares from and answers "keep
 * walking" for everything else. An element outside that set is the common case, not a defect, so
 * meeting one must neither stop the walk nor be reported.
 *
 * A `do` block is used as the unhandled element because no processor declares anything from one
 * directly; the block's body is reached through the call that owns it.
 */
class UnhandledElementTest : PlatformTestCase() {
    fun testVariableProcessorKeepsWalkingPastAnElementItDoesNotHandle() {
        val processor = object : Variable() {
            override fun executeOnVariable(match: PsiNamedElement, state: ResolveState): Boolean = true
        }

        assertKeepsWalkingSilently(processor)
    }

    fun testTypeProcessorKeepsWalkingPastAnElementItDoesNotHandle() {
        val processor = object : Type() {
            override fun executeOnType(definition: AtUnqualifiedNoParenthesesCall<*>, state: ResolveState) = true
            override fun executeOnParameter(parameter: PsiElement, state: ResolveState) = true
            override fun keepProcessing() = true
            override fun execute(typeDefinition: BeamTypeDefinition, state: ResolveState) = true
        }

        assertKeepsWalkingSilently(processor)
    }

    private fun assertKeepsWalkingSilently(processor: com.intellij.psi.scope.PsiScopeProcessor) {
        val file = myFixture.configureByText("unhandled.ex", "foo do\n  :ok\nend\n") as ElixirFile
        val doBlock = PsiTreeUtil.findChildOfType(file, ElixirDoBlock::class.java)
        assertNotNull("fixture did not parse to a do block", doBlock)

        val (keepProcessing, errors) = captureLoggedErrors {
            processor.execute(doBlock!!, ResolveState.initial())
        }

        assertEmpty("an unhandled element is not an error", errors)
        assertTrue("an unhandled element must not stop the walk", keepProcessing)
    }
}
