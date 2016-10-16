package org.elixir_lang.code_insight.completion.contributor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import org.elixir_lang.psi.ElixirFile;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.instanceOf;

public class CallDefinitionClause extends CompletionContributor {
    public CallDefinitionClause() {
        extend(
                CompletionType.BASIC,
                psiElement().inFile(instanceOf(ElixirFile.class)).afterLeaf("."),
                new org.elixir_lang.code_insight.completion.provider.CallDefinitionClause()
        );
    }
}
