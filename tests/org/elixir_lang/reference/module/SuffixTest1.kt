package org.elixir_lang.reference.module

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.psi.PsiPolyVariantReference
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.ElixirAlias

class SuffixTest : PlatformTestCase() {
    fun testCompletion() {
        myFixture.configureByFiles("completion.ex", "suffix.ex")
        myFixture.complete(CompletionType.BASIC, 1)
        val strings = myFixture.lookupElementStrings
        assertTrue(
                strings!!.containsAll(
                        listOf( // unaliased name
                                "Prefix.Suffix",  // aliased name
                                "Suffix"
                        )
                )
        )
        assertEquals(2, strings.size)
    }

    fun testReference() {
        myFixture.configureByFiles("reference.ex", "suffix.ex")
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
        assertEquals(resolveResults.size, 2)

        // defmodule
        assertEquals("defmodule Prefix.Suffix do\nend", resolveResults[0].element!!.text)
        // alias
        assertEquals("alias Prefix.Suffix", resolveResults[1].element!!.text)
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/module/suffix"
}
