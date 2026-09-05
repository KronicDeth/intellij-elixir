package org.elixir_lang.reference.callable

import org.elixir_lang.psi.ElixirIdentifier
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.call.Call

class Issue354Test : PlatformTestCase() {
    fun testLoggerLogstashBackend() {
        myFixture.configureByFile("logger_logstash_backend.ex")

        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull(elementAtCaret)
        assertInstanceOf(elementAtCaret, LeafPsiElement::class.java)

        val parent = elementAtCaret!!.parent
        assertNotNull(parent)
        assertInstanceOf(parent, ElixirIdentifier::class.java)

        val grandParent = parent.parent
        assertNotNull(grandParent)
        assertInstanceOf(grandParent, Call::class.java)
        val grandParentCall = grandParent as Call

        val reference = grandParentCall.reference
        assertNotNull(reference)
        assertInstanceOf(reference, PsiPolyVariantReference::class.java)
        val psiPolyVariantReference = reference as PsiPolyVariantReference

        val resolveResults = psiPolyVariantReference.multiResolve(true)

        /* `%{line: line, port: port} = context` reads `context`; it binds nothing, so the parameter is the only
           declaration the later use resolves to. */
        assertEquals(1, resolveResults.size)

        val resolveResult = resolveResults[0]
        assertTrue(resolveResult.isValidResult)
        val element = resolveResult.element
        assertNotNull(element)
        assertEquals("context = %{backend: true}", element!!.parent.text)
    }

    /*
     * Protected Instance Methods
     */
    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/callable/issue_354"
}
