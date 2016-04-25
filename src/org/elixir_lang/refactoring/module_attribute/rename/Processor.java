package org.elixir_lang.refactoring.module_attribute.rename;

import com.intellij.psi.PsiElement;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import org.jetbrains.annotations.NotNull;

import static org.elixir_lang.refactoring.module_attribute.rename.Handler.isAvailable;

public class Processor extends RenamePsiElementProcessor {
    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        return isAvailable(element);
    }
}
