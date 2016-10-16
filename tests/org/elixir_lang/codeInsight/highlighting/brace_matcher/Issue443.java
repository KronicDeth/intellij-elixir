package org.elixir_lang.codeInsight.highlighting.brace_matcher;

import com.intellij.codeInsight.highlighting.BraceMatcher;
import com.intellij.codeInsight.highlighting.BraceMatchingUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.elixir_lang.ElixirFileType;

public class Issue443 extends LightPlatformCodeInsightFixtureTestCase {
    /*
     * Tests
     */

    public void testDoBlock() {
        myFixture.configureByFile("do_block.ex");
        int offset = myFixture.getCaretOffset();

        Editor editor = myFixture.getEditor();
        HighlighterIterator iterator = ((EditorEx) editor).getHighlighter().createIterator(offset);
        FileType fileType = ElixirFileType.INSTANCE;

        CharSequence text = editor.getDocument().getCharsSequence();
        assertTrue("`do` not matched to `end`", BraceMatchingUtil.matchBrace(text, fileType, iterator, true, true));
    }

    public void testDoKeyword() {
        myFixture.configureByFile("do_keyword.ex");
        int offset = myFixture.getCaretOffset();

        Editor editor = myFixture.getEditor();
        HighlighterIterator iterator = ((EditorEx) editor).getHighlighter().createIterator(offset - 1);
        FileType fileType = ElixirFileType.INSTANCE;

        CharSequence text = editor.getDocument().getCharsSequence();
        assertFalse("`do:` matched to `end`", BraceMatchingUtil.matchBrace(text, fileType, iterator, true, true));
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/codeInsight/highlighting/brace_matcher/issue_443";
    }
}
