package org.elixir_lang.psi.scope.call_definition_clause

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import com.intellij.psi.PsiElement
import com.intellij.testFramework.UsefulTestCase
import com.intellij.psi.PsiReference
import com.intellij.codeInsight.lookup.LookupElement
import junit.framework.TestCase
import org.elixir_lang.psi.call.Call

class VariantsTest : LightPlatformCodeInsightFixtureTestCase() {
    fun testIssue453() {
        myFixture.configureByFiles("defmodule.ex")
        myFixture.complete(CompletionType.BASIC)
        val strings = myFixture.lookupElementStrings
        assertNotNull("Completion not shown", strings)
        assertEquals("Wrong number of completions", 0, strings!!.size)
    }

    fun testIssue462() {
        myFixture.configureByFiles("self_completion.ex")
        val head = myFixture
                .file
                .findElementAt(myFixture.caretOffset - 1)!!
                .parent
                .parent
        assertInstanceOf(head, Call::class.java)
        val reference = head.reference
        assertNotNull("Call definition head does not have a reference", reference)
        val variants = reference!!.variants
        var count = 0
        for (variant in variants) {
            if (variant is LookupElement) {
                if (variant.lookupString == "the_function_currently_being_defined") {
                    count += 1
                }
            }
        }
        assertEquals("There is at least one entry for the function currently being defined in variants", 0, count)
    }

    fun testIssue2073() {
        myFixture.configureByFile("callback.ex")
        val element = myFixture.file.findElementAt(myFixture.caretOffset - 1)!!.parent.parent
        assertInstanceOf(element, Call::class.java)
        assertEquals(2, element.reference!!.variants.size)
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/psi/scope/call_definition_clause/variants"
}
