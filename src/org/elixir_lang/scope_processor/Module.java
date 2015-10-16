package org.elixir_lang.scope_processor;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Module extends BaseScopeProcessor {
    /*
     * Fields
     */

    private final QualifiableAlias usage;
    private QualifiableAlias declaration = null;

    /*
     * Constructors
     */

    public Module(QualifiableAlias usage) {
        this.usage = usage;
    }

    @Nullable
    public QualifiableAlias declaration() {
        return declaration;
    }

    /**
     * @param element candidate element.
     * @param state   current state of resolver.
     * @return false to stop processing.
     */
    @Override
    public boolean execute(@NotNull final PsiElement element, @NotNull final ResolveState state) {
        boolean keepProcessing = true;

        if (element instanceof ElixirAlias) {
            ElixirAlias elementAsAlias = (ElixirAlias) element;

            if (elementAsAlias.fullyQualifiedName().equals(usage.fullyQualifiedName())) {
                PsiElement parent = element.getParent();

                if (parent instanceof ElixirAccessExpression) {
                    PsiElement grandparent = parent.getParent();

                    if (grandparent instanceof ElixirNoParenthesesOneArgument) {
                        PsiElement greatGrandparent = grandparent.getParent();

                        if (greatGrandparent instanceof ElixirUnmatchedUnqualifiedNoParenthesesCall) {
                            ElixirUnmatchedUnqualifiedNoParenthesesCall unmatchedUnqualifiedNoParenthesesCall = (ElixirUnmatchedUnqualifiedNoParenthesesCall) greatGrandparent;

                            if (unmatchedUnqualifiedNoParenthesesCall.isDefmodule()) {
                                declaration = elementAsAlias;
                                keepProcessing = false;
                            }
                        }
                    }
                }
            }
        }

        return keepProcessing;
    }
}
