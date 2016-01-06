package org.elixir_lang.psi;

import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * {@code atPrefixOperator !numeric ...}
 */
public interface AtNonNumericOperation extends ModuleAttributeNameable, PrefixOperation {
    @Contract(pure=true)
    @NotNull
    @Override
    PsiReference getReference();
}
