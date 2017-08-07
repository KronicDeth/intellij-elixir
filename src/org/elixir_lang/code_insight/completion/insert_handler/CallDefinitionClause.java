package org.elixir_lang.code_insight.completion.insert_handler;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

public class CallDefinitionClause implements InsertHandler<LookupElement> {
    /*
     * CONSTANTS
     */

    public static final InsertHandler<LookupElement> INSTANCE =
            new CallDefinitionClause();

    /*
     * Public Instance Methods
     */

    @Override
    public void handleInsert(@NotNull InsertionContext context,
                             @NotNull LookupElement item) {
        int tailOffset = context.getTailOffset();
        Document document = context.getDocument();
        int documentTextLength = document.getTextLength();
        boolean insertParentheses;

        if (documentTextLength > tailOffset) {
            String currentTail = document.getText(
                    new TextRange(tailOffset, tailOffset + 1)
            );
            char firstChar = currentTail.charAt(0);

            insertParentheses = firstChar != ' ' && firstChar != '(' && firstChar != '[';
        } else {
            insertParentheses = true;
        }

        if (insertParentheses) {
            context.getDocument().insertString(tailOffset, "()");
            // + 1 to put between the `(`  and `)`
            context.getEditor().getCaretModel().moveToOffset(tailOffset + 1);
        }
    }
}
