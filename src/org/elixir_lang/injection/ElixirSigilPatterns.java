package org.elixir_lang.injection;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.elixir_lang.psi.Sigil;
import org.jetbrains.annotations.NotNull;

public class ElixirSigilPatterns extends PlatformPatterns {
    public static ElementPattern<?> sigil() {
        return psiElement().inside(psiElement(Sigil.class));
    }

    @SuppressWarnings("unused")
    public static ElementPattern<?> sigilWithName(String name) {
        return and(sigil(), psiElement().with(new ElixirSigilPatterns.SigilWithName(name)));
    }

    public static class SigilWithName extends @NotNull PatternCondition<PsiElement> {
        Character expectedSigil;

        public SigilWithName(String name) {
            super(name);
            expectedSigil = name.charAt(0);
        }

        @Override
        public boolean accepts(@NotNull PsiElement psiElement, ProcessingContext processingContext) {
            if (psiElement instanceof Sigil) {
                return ((Sigil) psiElement).sigilName() == expectedSigil;
            }

            return false;
        }
    }
}
