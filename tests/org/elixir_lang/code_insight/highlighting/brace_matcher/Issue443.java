package org.elixir_lang.code_insight.highlighting.brace_matcher;

import com.intellij.codeInsight.highlighting.BraceMatcher;
import com.intellij.codeInsight.highlighting.BraceMatchingUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.elixir_lang.ElixirFileType;

import static com.intellij.codeInsight.highlighting.BraceMatchingUtil.getBraceMatcher;

public class Issue443 extends BasePlatformTestCase {
    /*
     * Tests
     */

    public void testDoBlock() {
        myFixture.configureByFile("do_block.ex");
        assertTrue("`do` not matched to `end`", isLBraceTokenBrace());
    }

    public void testDoKeyword() {
        myFixture.configureByFile("do_keyword.ex");

        assertFalse("`do:` matched to `end`", isLBraceTokenBrace());
    }

    public void testFnKeyword() {
        myFixture.configureByFile("fn_keyword.ex");

        assertFalse("`fn:` matched to `end`", isLBraceTokenBrace());
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/code_insight/highlighting/brace_matcher/issue_443";
    }

    /*
     * Private Instance Methods
     */

    private boolean isLBraceTokenBrace() {
        int offset = myFixture.getCaretOffset();
        Editor editor = myFixture.getEditor();
        CharSequence text = editor.getDocument().getCharsSequence();
        FileType fileType = ElixirFileType.INSTANCE;
        HighlighterIterator iterator = ((EditorEx) editor).getHighlighter().createIterator(offset);

        return BraceMatchingUtil.isLBraceToken(iterator, text, fileType);
    }
}
