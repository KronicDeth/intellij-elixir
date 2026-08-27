package org.elixir_lang.code_insight.highlighting.brace_matcher;

import com.intellij.codeInsight.highlighting.BraceMatcher;
import com.intellij.codeInsight.highlighting.BraceMatchingUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.fileTypes.FileType;
import org.elixir_lang.ElixirFileType;
import org.elixir_lang.PlatformTestCase;

public class Issue443Test extends PlatformTestCase {
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
        HighlighterIterator iterator = editor.getHighlighter().createIterator(offset);

        // Which matcher answered, before what it answered. The platform resolves it from the token's
        // language, and a lookup that misses silently yields the default matcher - which knows only
        // `(`, `[` and `{` and so returns false to everything. That would leave the two tests
        // expecting false passing for the wrong reason while only testDoBlock failed, pointing at
        // `do` rather than at the matcher that was never consulted.
        BraceMatcher braceMatcher = BraceMatchingUtil.getBraceMatcher(fileType, iterator);
        assertInstanceOf(braceMatcher, NonTrivial.class);

        return BraceMatchingUtil.isLBraceToken(iterator, text, fileType);
    }
}
