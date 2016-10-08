package org.elixir_lang.codeInsight.lookup.element;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class CallDefinitionClause {
    @NotNull
    public static LookupElement createWithSmartPointer(@NotNull String name, @NotNull PsiElement element) {
        return LookupElementBuilder.createWithSmartPointer(
                name,
                element
        ).withInsertHandler(
                org.elixir_lang.codeInsight.completion.insert_handler.CallDefinitionClause.INSTANCE
        ).withRenderer(
                new org.elixir_lang.codeInsight.lookup.element_renderer.CallDefinitionClause(
                        name
                )
        );
    }
}
