package org.elixir_lang.debugger.line_breakpoint

import com.intellij.xdebugger.breakpoints.XBreakpointType
import org.elixir_lang.PlatformTestCase

class TypeTest : PlatformTestCase() {
    private fun findType(): Type =
        XBreakpointType.EXTENSION_POINT_NAME.findExtension(Type::class.java)!!

    fun testCanPutAtInsideModule() {
        myFixture.configureByText(
            "test.ex",
            """
            defmodule Foo do
              :ok
            end
            """.trimIndent()
        )
        val type = findType()
        val file = myFixture.file.virtualFile
        // Line 1 (0-based) is `:ok` inside the module
        assertTrue(type.canPutAt(file, 1, project))
    }

    fun testCanPutAtTopLevel() {
        myFixture.configureByText(
            "test.ex",
            """
            IO.puts("hello")
            """.trimIndent()
        )
        val type = findType()
        val file = myFixture.file.virtualFile
        // Line 0 (0-based) is the top-level expression
        assertFalse(type.canPutAt(file, 0, project))
    }

    fun testCanPutAtNonElixirFile() {
        myFixture.configureByText("file.txt", "hello")
        val type = findType()
        val file = myFixture.file.virtualFile
        // processor(file) returns null for .txt files, short-circuits to false
        assertFalse(type.canPutAt(file, 0, project))
    }
}
