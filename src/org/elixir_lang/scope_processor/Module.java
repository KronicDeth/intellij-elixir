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

        if (element instanceof QualifiableAlias) {
            QualifiableAlias qualifiableAlias = (QualifiableAlias) element;
            String qualifiableAliasFullyQualifiedName = qualifiableAlias.fullyQualifiedName();

            if (qualifiableAlias.isModuleName() &&
                    qualifiableAliasFullyQualifiedName != null &&
                    qualifiableAliasFullyQualifiedName.equals(usage.fullyQualifiedName())) {
                declaration = qualifiableAlias;
                keepProcessing = false;
            }
        }

        return keepProcessing;
    }
}
