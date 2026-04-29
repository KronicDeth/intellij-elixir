package org.elixir_lang.documentation

import junit.framework.TestCase

class DocumentationMarkdownTextTest : TestCase() {
    fun testDedentDocumentationHeredocLinesRemovesDocIndentForListItems() {
        val dedented = dedentDocumentationHeredocLines(
            prefixLength = 4,
            lines = listOf("    * one\n", "    * two\n")
        )

        assertEquals("* one\n* two\n", dedented)
    }

    fun testDedentDocumentationHeredocLinesPreservesExtraIndentInCodeBlock() {
        val dedented = dedentDocumentationHeredocLines(
            prefixLength = 4,
            lines = listOf("        code\n", "            nested\n")
        )

        assertEquals("    code\n        nested\n", dedented)
    }

    fun testDedentDocumentationHeredocLinesKeepsBlankLines() {
        val dedented = dedentDocumentationHeredocLines(
            prefixLength = 4,
            lines = listOf("\n", "    * one\n", "\n")
        )

        assertEquals("\n* one\n\n", dedented)
    }
}
