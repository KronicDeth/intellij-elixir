package org.elixir_lang.code_insight.lookup.element;

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
                org.elixir_lang.code_insight.completion.insert_handler.CallDefinitionClause.INSTANCE
        ).withRenderer(
                new org.elixir_lang.code_insight.lookup.element_renderer.CallDefinitionClause(
                        name
                )
        );
    }
}
