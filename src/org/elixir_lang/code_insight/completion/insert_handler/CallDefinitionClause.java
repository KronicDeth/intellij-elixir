package org.elixir_lang.code_insight.completion.insert_handler;

import com.intellij.codeInsight.AutoPopupController;
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

            /* The caret now sits where the first argument goes, but nothing has asked for the parameter
               hint: an open lookup consumes the keystroke that accepted the completion, so the platform's
               typed handler - which asks on every `(` and `,` - never runs. Ask here, as the completion
               that inserted the parentheses. */
            AutoPopupController
                    .getInstance(context.getProject())
                    .autoPopupParameterInfo(context.getEditor(), null);
        }
    }
}
