package org.elixir_lang.codeInsight.completion.insert_handler;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

public class CallDefinitionClause implements InsertHandler<LookupElement> {
    /*
     * CONSTANTS
     */

    public static final InsertHandler<LookupElement> INSTANCE =
            new org.elixir_lang.codeInsight.completion.insert_handler.CallDefinitionClause();

    /*
     * Public Instance Methods
     */

    @Override
    public void handleInsert(@NotNull InsertionContext context,
                             @NotNull LookupElement item) {
        int tailOffset = context.getTailOffset();
        String currentTail = context.getDocument().getText(
                new TextRange(tailOffset, tailOffset + 1)
        );
        char firstChar = currentTail.charAt(0);

        if (firstChar != ' ' && firstChar != '(' && firstChar != '[') {
            context.getDocument().insertString(tailOffset, "()");
            // + 1 to put between the `(`  and `)`
            context.getEditor().getCaretModel().moveToOffset(tailOffset + 1);
        }
    }
}
