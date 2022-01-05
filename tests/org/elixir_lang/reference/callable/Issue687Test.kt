package org.elixir_lang.reference.callable

import org.elixir_lang.psi.ElixirIdentifier
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.call.Call

class Issue687Test : PlatformTestCase() {
    fun testRepeatedMapValueInMatch() {
        myFixture.configureByFiles("repeated_map_value_in_match.ex")
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
        assertInstanceOf(resolved, UnqualifiedNoArgumentsCall::class.java)
        assertEquals(resolved!!.text, "nine_id")
        assertEquals(resolved.parent.parent.text, "nine_id: nine_id")
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/callable/issue_687"
}
