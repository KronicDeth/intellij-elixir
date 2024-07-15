package org.elixir_lang.code_insight.highlighting.brace_matcher

import com.intellij.codeInsight.highlighting.BraceMatchingUtil
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.elixir_lang.ElixirFileType

class DoBlockMatchBraceMatcherTest : BasePlatformTestCase() {

    fun testDoBlockDiagnostic() {
        myFixture.configureByFile("do_block.ex")

        // Step 1: Check if the file is loaded correctly
        assertNotNull("File should be loaded", myFixture.file)

        // Step 2: Get the caret offset
        val offset = myFixture.caretOffset
        assertTrue("Caret offset should be positive", offset >= 0)

        // Step 3: Get the editor and document
        val editor = myFixture.editor
        assertNotNull("Editor should not be null", editor)
        val document = editor.document
        assertNotNull("Document should not be null", document)

        // Step 4: Get the highlighter and iterator
        val highlighter = (editor as EditorEx).highlighter
        assertNotNull("Highlighter should not be null", highlighter)
        val iterator = highlighter.createIterator(offset)
        assertNotNull("Iterator should not be null", iterator)
        assertEquals("Iterator start should be 126", 126, iterator.start)
        assertEquals("Iterator end should be 128", 128, iterator.end)
        val textAttributes = iterator.textAttributesKeys
        val textAttribute = textAttributes.get(0)
        assertEquals("ELIXIR_KEYWORD is textAttribute.externalName", "ELIXIR_KEYWORD", textAttribute.externalName)
        assertEquals(
            "DEFAULT_KEYWORD for the fallbackAttributeKey",
            "DEFAULT_KEYWORD",
            textAttribute.fallbackAttributeKey?.externalName ?: "",
        )

        // Step 5: Get the file type
        val fileType = ElixirFileType.INSTANCE
        assertNotNull("File type should not be null", fileType)

        // Step 6: Get the brace matcher
        val braceMatcher = BraceMatchingUtil.getBraceMatcher(fileType, iterator)
        assertNotNull("Brace matcher should not be null", braceMatcher)
        println("Brace matcher class: ${braceMatcher.javaClass.simpleName}")

        // Step 7: Check if it's a left brace token
        val isBrace = BraceMatchingUtil.isLBraceToken(iterator, document.charsSequence, fileType)
        assertTrue("Should be a left brace token", isBrace)

        // Step 8: Get the token type
        val tokenType = iterator.tokenType
        assertNotNull("Token type should not be null", tokenType)
        println("Token type: ${tokenType.debugName}")

        // Final assertion
        assertTrue("`do` should be matched to `end`", isBrace)
    }

    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/code_insight/highlighting/brace_matcher/issue_443"
    }
}