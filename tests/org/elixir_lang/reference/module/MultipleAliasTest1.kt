package org.elixir_lang.reference.module

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.openapi.util.Disposer
import com.intellij.psi.PsiPolyVariantReference
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.ElixirAlias
import java.lang.Exception

class MultipleAliasTest : PlatformTestCase() {
    fun testCompletion() {
        myFixture.configureByFiles("completion.ex", "multiple_alias_aye.ex", "multiple_alias_bee.ex")
        myFixture.complete(CompletionType.BASIC, 1)
        val strings = myFixture.lookupElementStrings
        assertTrue(
                strings!!.containsAll(
                        listOf( // unaliased names
                                "Prefix.MultipleAliasAye",  // aliased name
                                "MultipleAliasAye"
                        )
                )
        )
        assertEquals(2, strings.size)
    }

    fun testCompletionInSideCurlies() {
        myFixture.configureByFiles("completion_inside_curlies.ex", "multiple_alias_aye.ex", "multiple_alias_bee.ex")
        myFixture.complete(CompletionType.BASIC, 1)
        val strings = myFixture.lookupElementStrings
        assertNotNull(strings)
        assertEquals(2, strings!!.size)
        assertEquals("MultipleAliasAye", strings[0])
        assertEquals("MultipleAliasBee", strings[1])
    }

    fun testReference() {
        myFixture.configureByFiles("reference.ex", "multiple_alias_aye.ex", "multiple_alias_bee.ex")
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

        // defmodule
        assertEquals("defmodule Prefix.MultipleAliasAye do\nend", resolveResults[0].element!!.text)
        // alias
        assertEquals(
                "alias Prefix.{MultipleAliasAye, MultipleAliasBee}",
                resolveResults[1].element!!.text
        )
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/module/multiple_alias"

    @Throws(Exception::class)
    override fun tearDown() {
        super.tearDown()
        if (myFixture != null) {
            val project = project
            if (project != null && !project.isDisposed) {
                Disposer.dispose(project)
            }
        }
    }
}
