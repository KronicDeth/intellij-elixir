package org.elixir_lang.debugger.line_breakpoint.availability_processor

import org.elixir_lang.PlatformTestCase

class ElixirTest : PlatformTestCase() {
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
}
