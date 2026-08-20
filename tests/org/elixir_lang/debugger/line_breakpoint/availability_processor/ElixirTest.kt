package org.elixir_lang.debugger.line_breakpoint.availability_processor

import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.impl.isInsideModule

class ElixirTest : PlatformTestCase() {
    // region Phase A — baseline availability tests

    fun testAvailabilityInsideModule() {
        myFixture.configureByText(
            "test.ex",
            """
            defmodule Foo do
              <caret>:ok
            end
            """.trimIndent()
        )
        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)

        assertNotNull(elementAtCaret)

        val processor = Elixir()
        val keepIterating = processor.process(elementAtCaret!!)

        assertFalse("process should return false (stop iterating) for element inside module", keepIterating)
        assertTrue("isAvailable should be true for element inside module", processor.isAvailable)
    }

    fun testAvailabilityTopLevel() {
        myFixture.configureByText(
            "test.ex",
            """
            <caret>IO.puts("hello")
            """.trimIndent()
        )
        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)

        assertNotNull(elementAtCaret)

        val processor = Elixir()
        val keepIterating = processor.process(elementAtCaret!!)

        assertTrue("process should return true (keep iterating) for top-level element", keepIterating)
        assertFalse("isAvailable should be false for top-level element", processor.isAvailable)
    }

    // endregion

    // region Phase B — isInsideModule() tests

    fun testIsInsideModuleTrue() {
        myFixture.configureByText(
            "test.ex",
            """
            defmodule Foo do
              <caret>:ok
            end
            """.trimIndent()
        )
        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)

        assertNotNull(elementAtCaret)
        assertTrue("isInsideModule should return true for element inside defmodule", elementAtCaret!!.isInsideModule())
    }

    fun testIsInsideModuleFalseTopLevel() {
        myFixture.configureByText(
            "test.ex",
            """
            <caret>IO.puts("hello")
            """.trimIndent()
        )
        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)

        assertNotNull(elementAtCaret)
        assertFalse("isInsideModule should return false for top-level element", elementAtCaret!!.isInsideModule())
    }

    // endregion
}
