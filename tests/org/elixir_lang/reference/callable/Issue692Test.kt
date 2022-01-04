package org.elixir_lang.reference.callable

import org.elixir_lang.psi.ElixirIdentifier
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.call.Call

class Issue692Test : PlatformTestCase() {
    fun testUnresolvedAtTopOfFile() {
        myFixture.configureByFiles("unresolved_at_top_of_file.ex")
        val elementAtCaret = myFixture
                .file
                .findElementAt(myFixture.caretOffset)
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

        val resolved = reference!!.resolve()
        assertNotNull(resolved)
        assertTrue(resolved!!.isEquivalentTo(grandParent))
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/callable/issue_692"
}
