package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * Alias that responds to `fullyQualifiedName`
 */
public interface QualifiableAlias extends PsiElement {
    @Nullable
    String fullyQualifiedName();
}
