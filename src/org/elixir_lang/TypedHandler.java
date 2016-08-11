package org.elixir_lang;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.psi.ElixirFile;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;

public class TypedHandler extends TypedHandlerDelegate {
    /**
     * Called after the specified character typed by the user has been inserted in the editor.
     *
     * @param charTyped the character that was typed
     * @param project the project in which the {@code file} exists
     * @param editor the editor that has the {@code file} open
     * @param file the file into which the {@code charTyped} was typed
     */
    @Override
    public Result charTyped(char charTyped, Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        Result result = Result.CONTINUE;

        if (file instanceof ElixirFile) {
            if (charTyped == ' ') {
                int caret = editor.getCaretModel().getOffset();

                // "(do|fn)<space><caret>"
                if (caret > 2) {
                    final EditorHighlighter highlighter = ((EditorEx)editor).getHighlighter();
                    HighlighterIterator iterator = highlighter.createIterator(caret - 3);
                    IElementType tokenType = iterator.getTokenType();

                    if (tokenType == ElixirTypes.DO || tokenType == ElixirTypes.FN) {
                        editor.getDocument().insertString(caret, " end");
                        result = Result.STOP;
                    }
                }
            }
        }

        return result;
    }
}
