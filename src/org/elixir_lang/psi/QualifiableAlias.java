package org.elixir_lang.psi;

import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.Nullable;

/**
 * Alias that responds to `fullyQualifiedName`
 */
public interface QualifiableAlias extends PsiNameIdentifierOwner {
    @Nullable
    String fullyQualifiedName();
}
