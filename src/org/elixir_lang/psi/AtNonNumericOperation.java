package org.elixir_lang.psi;

import com.intellij.psi.PsiReference;
import org.elixir_lang.psi.operation.Prefix;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * {@code atPrefixOperator !numeric ...}
 */
public interface AtNonNumericOperation extends ModuleAttributeNameable, Prefix {
    @Contract(pure=true)
    @Nullable
    @Override
    PsiReference getReference();
}
