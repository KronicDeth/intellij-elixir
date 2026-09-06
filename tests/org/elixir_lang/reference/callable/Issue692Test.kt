package org.elixir_lang.reference.callable

import org.elixir_lang.psi.ElixirIdentifier
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.call.Call

/**
 * `left=right` as the whole file once logged "Scope is not lastParent's parent" while resolving `right`. Nothing
 * binds `right`, so it resolves to nothing, and doing so must not report.
 */
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

        val (resolved, errors) = captureLoggedErrors { reference!!.resolve() }

        assertEmpty(errors)
        assertNull("a read with nothing bound above it resolved to something", resolved)
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/callable/issue_692"
}
