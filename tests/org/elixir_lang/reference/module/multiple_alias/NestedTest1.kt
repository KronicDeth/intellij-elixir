package org.elixir_lang.reference.module.multiple_alias

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.openapi.util.Disposer
import com.intellij.psi.PsiPolyVariantReference
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.QualifiedAlias
import java.lang.Exception

class NestedTest : PlatformTestCase() {
    /*
     * Tests
     */
    fun testCompletion() {
        myFixture.configureByFiles("completion.ex", "multiple_alias_aye.ex", "multiple_alias_bee.ex", "nested.ex")
        myFixture.complete(CompletionType.BASIC, 1)
        val strings = myFixture.lookupElementStrings
        assertNotNull(strings)
        assertEquals(1, strings!!.size)
        assertEquals("Nested", strings[0])
    }

    fun testReference() {
        myFixture.configureByFiles("reference.ex", "multiple_alias_aye.ex", "multiple_alias_bee.ex", "nested.ex")
        val alias = myFixture
                .file
                .findElementAt(myFixture.caretOffset)!!
                .parent
                .prevSibling
        assertInstanceOf(alias, QualifiedAlias::class.java)

        val polyVariantReference = alias.reference as PsiPolyVariantReference?
        assertNotNull(polyVariantReference)

        val resolveResults = polyVariantReference!!.multiResolve(false)
        assertEquals(2, resolveResults.size)

        // defmodule
        assertEquals("defmodule Prefix.MultipleAliasAye.Nested do\nend", resolveResults[0].element!!.text)
        // alias
        assertEquals(
                "alias Prefix.{MultipleAliasAye, MultipleAliasBee}",
                resolveResults[1].element!!.text
        )
    }

    /*
     * Protected Instance Methods
     */
    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/module/multiple_alias/nested"

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
