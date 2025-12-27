package org.elixir_lang.reference.module

import com.intellij.psi.PsiPolyVariantReference
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.ElixirAlias

class AsTest : PlatformTestCase() {
    /*
     * Tests
     */
    fun testCompletion() {
        val completionVariants = myFixture.getCompletionVariants(
                "completion.ex",
                "suffix1.ex",
                "suffix2.ex"
        )
        assertTrue(
                "AsA was not completed to AsAlias1",
                completionVariants!!.contains("AsAlias1")
        )
        assertTrue(
                "AsA was not completed to AsAlias2",
                completionVariants.contains("AsAlias2")
        )
        assertEquals(2, completionVariants.size)
    }

    fun testReference() {
        myFixture.configureByFiles("reference.ex", "suffix1.ex")
        val alias = myFixture
                .file
                .findElementAt(myFixture.caretOffset)!!
                .parent
                .prevSibling
                .firstChild
        assertInstanceOf(alias, ElixirAlias::class.java)
        val polyVariantReference = alias.reference as PsiPolyVariantReference?
        assertNotNull(polyVariantReference)
        val resolveResults = polyVariantReference!!.multiResolve(false)
        assertEquals(2, resolveResults.size)

        // defmodule ..
        assertEquals("defmodule Prefix.Suffix1 do\nend", resolveResults[0].element!!.text)
        // alias ..
        assertEquals("alias Prefix.Suffix1, as: As", resolveResults[1].element!!.text)
    }

    /*
     * Protected Instance Methods
     */
    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/reference/module/as"
    }
}
