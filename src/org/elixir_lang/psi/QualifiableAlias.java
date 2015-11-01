package org.elixir_lang.psi;

import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.Nullable;

/**
 * An alias that may or may not be qualified.
 */
public interface QualifiableAlias extends MaybeModuleName, PsiNameIdentifierOwner {
    @Nullable
    String fullyQualifiedName();
}
