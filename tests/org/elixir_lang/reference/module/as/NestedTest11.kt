package org.elixir_lang.reference.module.`as`

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.openapi.util.Disposer
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.elixir_lang.psi.QualifiedAlias

class NestedTest : BasePlatformTestCase() {
    fun testCompletion() {
        myFixture.configureByFiles("completion.ex", "suffix.ex", "nested.ex")
        myFixture.complete(CompletionType.BASIC, 1)
        val strings = myFixture.lookupElementStrings
        assertNotNull("Completion lookup shown", strings)
        assertEquals(1, strings!!.size)
        assertEquals("Nested", strings[0])
    }

    fun testReference() {
        myFixture.configureByFiles("reference.ex", "suffix.ex", "nested.ex")
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
        assertEquals("defmodule Prefix.Suffix.Nested do\nend", resolveResults[0].element!!.text)
        // alias
        assertEquals("alias Prefix.Suffix, as: As", resolveResults[1].element!!.text)
    }

    /*
     * Protected Instance Methods
     */
    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/module/as/nested"

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
