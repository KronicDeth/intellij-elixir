package org.elixir_lang.psi.scope.call_definition_clause

import com.intellij.codeInsight.completion.CompletionType
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
        myFixture.complete(CompletionType.BASIC)
        val variants = myFixture.lookupElementStrings.orEmpty()
        assertFalse(
            "Function currently being defined should not appear in completion variants",
            variants.contains("the_function_currently_being_defined")
        )
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
