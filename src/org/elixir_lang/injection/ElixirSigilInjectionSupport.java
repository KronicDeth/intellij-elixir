package org.elixir_lang.injection;

import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.intellij.plugins.intelliLang.inject.AbstractLanguageInjectionSupport;

public final class ElixirSigilInjectionSupport extends AbstractLanguageInjectionSupport {
    @NonNls public static final String ELIXIR_SUPPORT_ID = "elixir";

    @Override
    @NotNull
    public String getId() {
        return ELIXIR_SUPPORT_ID;
    }

    @Override
    public Class<?> @NotNull [] getPatternClasses() {
        return new Class[] { ElixirSigilPatterns.class };
    }

    @Override
    public boolean isApplicableTo(com.intellij.psi.PsiLanguageInjectionHost host) {
        return true;
    }

    @Override
    public boolean useDefaultInjector(final PsiLanguageInjectionHost host) {
        return true;
    }
}
