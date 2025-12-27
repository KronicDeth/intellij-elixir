package org.elixir_lang.psi.scope.call_definition_clause

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.lookup.LookupElementPresentation
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.call.Call

class VariantsTest : PlatformTestCase() {
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
        val variants = element.reference!!.variants.filterIsInstance<LookupElementBuilder>()

        val functionCallbackVariant = variants.find { it.lookupString == "function_callback" }
        assertNotNull(functionCallbackVariant)

        val functionCallbackLookupElementPresentation = LookupElementPresentation()
        functionCallbackVariant!!.renderElement(functionCallbackLookupElementPresentation)
        assertEquals("function_callback", functionCallbackLookupElementPresentation.itemText)
        assertEquals("/0 (/src/callback.ex defmodule Behaviour)", functionCallbackLookupElementPresentation.tailText)

        val macroCallbackVariant = variants.find { it.lookupString == "macro_callback" }
        assertNotNull(macroCallbackVariant)

        val macroCallbackLookupElementPresentation = LookupElementPresentation()
        macroCallbackVariant!!.renderElement(macroCallbackLookupElementPresentation)
        assertEquals("macro_callback", macroCallbackLookupElementPresentation.itemText)
        assertEquals("/1 (/src/callback.ex defmodule Behaviour)", macroCallbackLookupElementPresentation.tailText)
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/psi/scope/call_definition_clause/variants"
}
