package org.elixir_lang.code_insight.completion.contributor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import org.elixir_lang.psi.ElixirFile;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.instanceOf;

final class ModuleAttribute extends CompletionContributor {
    public ModuleAttribute() {
        extend(
                CompletionType.BASIC,
                psiElement().inFile(instanceOf(ElixirFile.class)),
                new org.elixir_lang.code_insight.completion.provider.ModuleAttribute()
        );
    }
}
