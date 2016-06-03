package org.elixir_lang.refactoring.variable.rename;

import com.intellij.psi.PsiElement;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import org.jetbrains.annotations.NotNull;

import static org.elixir_lang.refactoring.variable.rename.Handler.isAvailable;

public class Processor extends RenamePsiElementProcessor {
    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        return isAvailable(element);
    }
}
